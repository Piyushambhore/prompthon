package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.models.*
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * Simulates DigiLocker API integration for the hackathon.
 *
 * In production this would call the real DigiLocker Partner API:
 *   - OAuth 2.0 Authorization Code flow
 *   - Pull URI API to fetch issued documents
 *   - eAadhaar, Income Certificate, Caste Certificate, Land Records etc.
 *
 * Sandbox docs: https://developers.digilocker.gov.in/
 *
 * For the prototype we simulate realistic responses so the Android client
 * can demonstrate the full flow end-to-end.
 */
class DigiLockerService {

    private val logger = LoggerFactory.getLogger(DigiLockerService::class.java)

    // Simulated token store (in production: Redis / DB)
    private val activeSessions = mutableMapOf<String, SimulatedSession>()

    // ── Step 1: Generate DigiLocker authorization URL ───────────────

    fun initiateAuth(callbackUrl: String): DigiLockerAuthResponse {
        val state = UUID.randomUUID().toString().take(16)

        activeSessions[state] = SimulatedSession(
            state = state,
            status = "pending"
        )

        // In production this would be the real DigiLocker OAuth URL:
        // https://digilocker.meripehchaan.gov.in/public/oauth2/1/authorize
        val authUrl = "https://digilocker.meripehchaan.gov.in/public/oauth2/1/authorize" +
                "?response_type=code" +
                "&client_id=JANSAARTHI_SANDBOX" +
                "&redirect_uri=$callbackUrl" +
                "&state=$state" +
                "&scope=openid"

        logger.info("DigiLocker auth initiated, state=$state")

        return DigiLockerAuthResponse(
            authorizationUrl = authUrl,
            state = state,
            message = "Redirect the user to this URL to authorize DigiLocker access."
        )
    }

    // ── Step 2: Exchange authorization code for token + pull docs ───

    fun handleCallback(request: DigiLockerCallbackRequest): DigiLockerTokenResponse {
        logger.info("DigiLocker callback received, state=${request.state}")

        // In production: POST to DigiLocker token endpoint with auth code
        // For prototype: simulate successful token exchange
        val session = activeSessions[request.state]
        if (session != null) {
            session.status = "authenticated"
            session.token = "dl_token_${UUID.randomUUID().toString().take(12)}"
        }

        return DigiLockerTokenResponse(
            accessToken = session?.token ?: "dl_token_demo",
            digiLockerId = "DL-${UUID.randomUUID().toString().take(8).uppercase()}",
            name = "Demo User",
            message = "DigiLocker authentication successful. Documents are being fetched."
        )
    }

    // ── Step 3: Pull user's documents from DigiLocker ──────────────

    fun pullDocuments(accessToken: String): List<DigiLockerDocument> {
        logger.info("Pulling documents from DigiLocker (simulated)")

        // In production: call DigiLocker Pull URI API for each document type
        // GET https://digilocker.meripehchaan.gov.in/public/oauth2/2/pull/uri
        //
        // Documents available via DigiLocker:
        //   - eAadhaar (UIDAI)
        //   - PAN (CBDT)
        //   - Driving License (State Transport)
        //   - Class X/XII Marksheet (CBSE/State Board)
        //   - Income Certificate (State Revenue Dept)
        //   - Caste Certificate (State Social Welfare)
        //   - Domicile Certificate
        //   - Land Records (State Land Revenue)

        return listOf(
            DigiLockerDocument(
                documentType = "AADHAAR",
                documentName = "Aadhaar Card",
                issuer = "Unique Identification Authority of India (UIDAI)",
                verified = true,
                issuedDate = "2020-03-15",
                documentUri = "in.gov.uidai-ADHAR-XXXXXXXX1234"
            ),
            DigiLockerDocument(
                documentType = "PAN",
                documentName = "PAN Card",
                issuer = "Central Board of Direct Taxes (CBDT)",
                verified = true,
                issuedDate = "2019-06-20",
                documentUri = "in.gov.cbdt-PAN-ABCDE1234F"
            ),
            DigiLockerDocument(
                documentType = "INCOME_CERT",
                documentName = "Income Certificate",
                issuer = "Revenue Department, Maharashtra",
                verified = true,
                issuedDate = "2025-01-10",
                documentUri = "in.gov.mh.revenue-INCOMECERT-2025001234"
            ),
            DigiLockerDocument(
                documentType = "CASTE_CERT",
                documentName = "Caste Certificate",
                issuer = "Social Justice Department, Maharashtra",
                verified = true,
                issuedDate = "2024-08-22",
                documentUri = "in.gov.mh.sjsa-CASTECERT-2024005678"
            ),
            DigiLockerDocument(
                documentType = "MARKSHEET_12",
                documentName = "Class XII Marksheet",
                issuer = "Maharashtra State Board of Secondary Education",
                verified = true,
                issuedDate = "2024-06-15",
                documentUri = "in.gov.mh.msbse-MARKSHEET12-2024009876"
            ),
            DigiLockerDocument(
                documentType = "DOMICILE",
                documentName = "Domicile Certificate",
                issuer = "Revenue Department, Maharashtra",
                verified = true,
                issuedDate = "2024-11-05",
                documentUri = "in.gov.mh.revenue-DOMICILE-2024003456"
            )
        )
    }

    // ── Build verified profile from DigiLocker documents ───────────

    fun buildProfileFromDocuments(documents: List<DigiLockerDocument>): VerifiedProfile {
        // In production: parse actual XML/JSON from DigiLocker pull responses
        // to extract demographic data. For prototype: simulate extraction.

        val dataSources = mutableListOf<DataSourceInfo>()

        // From Aadhaar → age, state, district, name, gender
        val hasAadhaar = documents.any { it.documentType == "AADHAAR" }
        if (hasAadhaar) {
            dataSources.addAll(listOf(
                DataSourceInfo("name", "DIGILOCKER_AADHAAR", true),
                DataSourceInfo("age", "DIGILOCKER_AADHAAR", true),
                DataSourceInfo("gender", "DIGILOCKER_AADHAAR", true),
                DataSourceInfo("state", "DIGILOCKER_AADHAAR", true),
                DataSourceInfo("district", "DIGILOCKER_AADHAAR", true)
            ))
        }

        // From Income Certificate → annualIncome, category
        val hasIncome = documents.any { it.documentType == "INCOME_CERT" }
        if (hasIncome) {
            dataSources.add(DataSourceInfo("annualIncome", "DIGILOCKER_INCOME_CERT", true))
        }

        // From Caste Certificate → category
        val hasCaste = documents.any { it.documentType == "CASTE_CERT" }
        if (hasCaste) {
            dataSources.add(DataSourceInfo("category", "DIGILOCKER_CASTE_CERT", true))
        }

        return VerifiedProfile(
            name = "Demo User",
            age = 19,
            gender = "Male",
            state = "Maharashtra",
            district = "Kolhapur",
            category = if (hasCaste) "SC" else null,
            annualIncome = if (hasIncome) 180000L else null,
            occupation = null,   // Not available from DigiLocker — user must self-declare
            isStudent = false,   // Not available — user must self-declare
            documents = documents,
            dataSources = dataSources
        )
    }

    // ── Internal ───────────────────────────────────────────────────

    private data class SimulatedSession(
        val state: String,
        var status: String,
        var token: String? = null
    )
}
