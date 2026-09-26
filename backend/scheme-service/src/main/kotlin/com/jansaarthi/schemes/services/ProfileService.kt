package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.models.*
import org.slf4j.LoggerFactory

/**
 * Unified profile-building service that supports three input modes:
 *
 *  1. **manual**      — User fills in every field via the Android form.
 *  2. **digilocker**  — OAuth-verified documents auto-populate the profile.
 *  3. **farmer**      — Agriculture-specific flow with PM-KISAN / land records.
 *
 * The output is always a [VerifiedProfile] with per-field data-source tracking
 * so the Android UI can show verified (green badge) vs self-declared (grey badge).
 */
class ProfileService(
    private val digiLockerService: DigiLockerService,
    private val farmerService: FarmerService
) {

    private val logger = LoggerFactory.getLogger(ProfileService::class.java)

    fun buildProfile(request: ProfileBuildRequest): ProfileEligibilityResponse {
        return when (request.mode.lowercase()) {
            "digilocker" -> buildFromDigiLocker(request)
            "farmer"     -> buildFromFarmer(request)
            else         -> buildFromManual(request)
        }
    }

    // ── Mode 1: Manual input ───────────────────────────────────────

    private fun buildFromManual(request: ProfileBuildRequest): ProfileEligibilityResponse {
        logger.info("Building profile from manual input")

        val dataSources = listOf("age", "state", "district", "occupation", "annualIncome", "category")
            .map { DataSourceInfo(it, "SELF_DECLARED", false) }

        val documents = request.availableDocuments.map { docName ->
            DigiLockerDocument(
                documentType = docName.uppercase().replace(" ", "_"),
                documentName = docName,
                issuer = "Self-declared by user",
                verified = false
            )
        }

        val profile = VerifiedProfile(
            name = "User",
            age = request.age ?: 0,
            state = request.state ?: "",
            district = request.district,
            category = request.category,
            annualIncome = request.annualIncome,
            occupation = request.occupation,
            isStudent = request.isStudent,
            documents = documents,
            dataSources = dataSources
        )

        return ProfileEligibilityResponse(
            profile = profile,
            mode = "manual",
            eligibleSchemeCount = 0,    // Caller runs eligibility separately
            averageDocumentReadiness = 0,
            message = "Profile built from self-declared data. " +
                    "Connect DigiLocker for verified documents and faster processing."
        )
    }

    // ── Mode 2: DigiLocker-connected ───────────────────────────────

    private fun buildFromDigiLocker(request: ProfileBuildRequest): ProfileEligibilityResponse {
        logger.info("Building profile from DigiLocker")

        val token = request.digiLockerToken
            ?: return ProfileEligibilityResponse(
                profile = VerifiedProfile(
                    name = "", age = 0, state = "", documents = emptyList(), dataSources = emptyList()
                ),
                mode = "digilocker",
                eligibleSchemeCount = 0,
                averageDocumentReadiness = 0,
                message = "DigiLocker token is required. Call /api/profile/digilocker/auth first."
            )

        // Pull verified documents from DigiLocker
        val documents = digiLockerService.pullDocuments(token)

        // Extract profile from documents
        val profile = digiLockerService.buildProfileFromDocuments(documents)

        // Override with any manual supplements (occupation, student status)
        val enriched = profile.copy(
            occupation = request.occupation ?: profile.occupation,
            isStudent = request.isStudent,
            dataSources = profile.dataSources + listOfNotNull(
                request.occupation?.let { DataSourceInfo("occupation", "SELF_DECLARED", false) },
                DataSourceInfo("isStudent", "SELF_DECLARED", false)
            )
        )

        val verifiedCount = enriched.dataSources.count { it.verified }
        val totalFields = enriched.dataSources.size

        return ProfileEligibilityResponse(
            profile = enriched,
            mode = "digilocker",
            eligibleSchemeCount = 0,
            averageDocumentReadiness = if (totalFields > 0) (verifiedCount * 100 / totalFields) else 0,
            message = "Profile built from DigiLocker-verified documents. " +
                    "$verifiedCount out of $totalFields data fields are government-verified. " +
                    "Occupation and student status require self-declaration."
        )
    }

    // ── Mode 3: Farmer flow ────────────────────────────────────────

    private fun buildFromFarmer(request: ProfileBuildRequest): ProfileEligibilityResponse {
        logger.info("Building farmer profile")

        val farmerRequest = request.farmerProfile
            ?: return ProfileEligibilityResponse(
                profile = VerifiedProfile(
                    name = "", age = 0, state = "", documents = emptyList(), dataSources = emptyList()
                ),
                mode = "farmer",
                eligibleSchemeCount = 0,
                averageDocumentReadiness = 0,
                message = "farmerProfile object is required when mode = 'farmer'."
            )

        // Pull base documents from DigiLocker if token provided
        val baseDocuments = mutableListOf<DigiLockerDocument>()
        if (request.digiLockerToken != null) {
            baseDocuments.addAll(digiLockerService.pullDocuments(request.digiLockerToken))
        }
        // Include any user-declared documents not already pulled
        for (docName in request.availableDocuments) {
            if (baseDocuments.none { it.documentName.equals(docName, ignoreCase = true) }) {
                baseDocuments.add(
                    DigiLockerDocument(
                        documentType = docName.uppercase().replace(" ", "_"),
                        documentName = docName,
                        issuer = "Self-declared",
                        verified = false
                    )
                )
            }
        }

        val farmerResponse = farmerService.buildFarmerProfile(farmerRequest, baseDocuments)

        return ProfileEligibilityResponse(
            profile = farmerResponse.profile,
            mode = "farmer",
            eligibleSchemeCount = farmerResponse.recommendedSchemeIds.size,
            averageDocumentReadiness = 0,
            message = "Farmer profile built. Category: ${farmerResponse.farmerCategory}. " +
                    "PM-KISAN status: ${farmerResponse.pmKisanStatus.beneficiaryStatus}. " +
                    "${farmerResponse.recommendedSchemeIds.size} agriculture schemes recommended."
        )
    }
}
