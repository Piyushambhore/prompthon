package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.models.*
import org.slf4j.LoggerFactory

/**
 * Handles farmer-specific profile enrichment.
 *
 * In production this would integrate with:
 *   - PM-KISAN API (https://pmkisan.gov.in/) — beneficiary status check
 *   - State land record portals (e.g. MahaBhulekh for Maharashtra)
 *   - KCC (Kisan Credit Card) verification via banks
 *   - Soil Health Card database
 *
 * For the hackathon we simulate these integrations with realistic responses.
 */
class FarmerService {

    private val logger = LoggerFactory.getLogger(FarmerService::class.java)

    /**
     * Build a farmer-enriched profile with PM-KISAN status and land classification.
     */
    fun buildFarmerProfile(
        request: FarmerProfileRequest,
        baseDocuments: List<DigiLockerDocument>
    ): FarmerProfileResponse {
        logger.info("Building farmer profile for state=${request.state}")

        // ── 1. Determine farmer category by land holding ───────────
        val farmerCategory = classifyFarmer(request.landOwnership)

        // ── 2. Check PM-KISAN beneficiary status ───────────────────
        val pmKisanStatus = checkPmKisanStatus(request.pmKisanBeneficiary)

        // ── 3. Add farmer-specific documents ───────────────────────
        val farmerDocs = mutableListOf<DigiLockerDocument>()
        farmerDocs.addAll(baseDocuments)

        // If Aadhaar is linked, add Aadhaar Card if not present
        if (request.aadhaarLinked && farmerDocs.none { it.documentType == "AADHAAR" }) {
            farmerDocs.add(DigiLockerDocument(
                documentType = "AADHAAR",
                documentName = "Aadhaar Card",
                issuer = "Unique Identification Authority of India (UIDAI)",
                verified = true,
                issuedDate = "2020-01-15",
                documentUri = "in.gov.uidai-ADHAR-VERIFIED"
            ))
        }

        if (request.landOwnership?.hasLand == true) {
            farmerDocs.add(DigiLockerDocument(
                documentType = "LAND_RECORD",
                documentName = "Land Ownership Records",
                issuer = "Revenue Department, ${request.state}",
                verified = request.aadhaarLinked,
                issuedDate = "2025-06-01",
                documentUri = "in.gov.${request.state.lowercase().take(2)}.revenue-LAND712-2025"
            ))
        }

        if (request.kisanCreditCard == true) {
            farmerDocs.add(DigiLockerDocument(
                documentType = "KCC",
                documentName = "Kisan Credit Card",
                issuer = "Bank (via NABARD)",
                verified = true,
                issuedDate = "2024-12-01"
            ))
        }

        if (pmKisanStatus.registered) {
            farmerDocs.add(DigiLockerDocument(
                documentType = "PM_KISAN_CERT",
                documentName = "PM-KISAN Beneficiary Certificate",
                issuer = "Ministry of Agriculture & Farmers Welfare",
                verified = true,
                issuedDate = pmKisanStatus.lastInstallment
            ))
            // PM-KISAN DBT registration verifies active bank account
            if (farmerDocs.none { it.documentType == "BANK_PASSBOOK" }) {
                farmerDocs.add(DigiLockerDocument(
                    documentType = "BANK_PASSBOOK",
                    documentName = "Bank Passbook",
                    issuer = "Public Sector Bank (DBT Linked)",
                    verified = true,
                    issuedDate = "2023-05-10"
                ))
            }
        }

        // ── 4. Build data source tracking ──────────────────────────
        val dataSources = mutableListOf(
            DataSourceInfo("occupation", "FARMER_SELF_DECLARED", false),
            DataSourceInfo("farmerCategory", "LAND_RECORD_CLASSIFICATION", request.landOwnership?.hasLand == true)
        )

        if (pmKisanStatus.registered) {
            dataSources.add(DataSourceInfo("pmKisanStatus", "PM_KISAN_API", true))
        }
        if (request.aadhaarLinked) {
            dataSources.add(DataSourceInfo("identity", "DIGILOCKER_AADHAAR", true))
        }

        // ── 5. Assemble full profile ───────────────────────────────
        val profile = VerifiedProfile(
            name = "Farmer User",
            age = 45,       // Would come from Aadhaar in production
            gender = "Male",
            state = request.state,
            district = request.district,
            category = null,    // Would come from caste cert
            annualIncome = estimateFarmerIncome(farmerCategory),
            occupation = "farmer",
            isStudent = false,
            documents = farmerDocs,
            dataSources = dataSources
        )

        // ── 6. Recommend relevant agriculture schemes ──────────────
        val recommendedSchemes = getRecommendedFarmerSchemes(farmerCategory, request.state)

        return FarmerProfileResponse(
            profile = profile,
            farmerCategory = farmerCategory,
            pmKisanStatus = pmKisanStatus,
            recommendedSchemeIds = recommendedSchemes
        )
    }

    // ── PM-KISAN status check (simulated) ──────────────────────────

    private fun checkPmKisanStatus(beneficiary: Boolean?): PmKisanStatus {
        return when (beneficiary) {
            true -> PmKisanStatus(
                registered = true,
                beneficiaryStatus = "active",
                lastInstallment = "2025-08-01"
            )
            false -> PmKisanStatus(
                registered = true,
                beneficiaryStatus = "pending_verification",
                lastInstallment = null
            )
            null -> PmKisanStatus(
                registered = false,
                beneficiaryStatus = "not_registered",
                lastInstallment = null
            )
        }
    }

    // ── Farmer classification (per GoI norms) ──────────────────────
    // Marginal: < 1 hectare (2.47 acres)
    // Small:    1–2 hectares (2.47–4.94 acres)
    // Semi-Medium: 2–4 hectares
    // Medium:   4–10 hectares
    // Large:    > 10 hectares

    private fun classifyFarmer(land: LandInfo?): String {
        if (land == null || !land.hasLand) return "landless"

        val acres = land.landAreaAcres ?: return "marginal"
        return when {
            acres < 2.47  -> "marginal"
            acres < 4.94  -> "small"
            acres < 9.88  -> "semi-medium"
            acres < 24.7  -> "medium"
            else          -> "large"
        }
    }

    private fun estimateFarmerIncome(category: String): Long {
        return when (category) {
            "landless"    -> 72000L
            "marginal"    -> 100000L
            "small"       -> 150000L
            "semi-medium" -> 250000L
            "medium"      -> 500000L
            "large"       -> 1000000L
            else          -> 100000L
        }
    }

    // ── Scheme recommendations based on farmer type ────────────────

    private fun getRecommendedFarmerSchemes(category: String, state: String): List<String> {
        val schemes = mutableListOf<String>()

        // PM-KISAN — all farmers with cultivable land
        if (category != "landless") {
            schemes.add("SCH005") // PM-KISAN
        }

        // PM Fasal Bima — all cultivators
        if (category != "landless") {
            schemes.add("SCH006") // PM Fasal Bima Yojana
        }

        // KCC — all farmers
        schemes.add("SCH007") // Kisan Credit Card

        // MGNREGA — landless and marginal
        if (category in listOf("landless", "marginal")) {
            schemes.add("SCH015") // MGNREGA
        }

        // PM Ujjwala — low income farmers
        if (category in listOf("landless", "marginal", "small")) {
            schemes.add("SCH018") // PM Ujjwala
        }

        // PM-JAY health — income eligible
        schemes.add("SCH013") // Ayushman Bharat

        return schemes.distinct()
    }
}
