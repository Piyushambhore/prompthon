package com.jansaarthi.schemes.routes

import com.jansaarthi.schemes.models.*
import com.jansaarthi.schemes.services.DigiLockerService
import com.jansaarthi.schemes.services.DocumentService
import com.jansaarthi.schemes.services.EligibilityService
import com.jansaarthi.schemes.services.FarmerService
import com.jansaarthi.schemes.services.ProfileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val MAX_BODY_BYTES = 1_048_576L

fun Route.profileRoutes(
    profileService: ProfileService,
    digiLockerService: DigiLockerService,
    farmerService: FarmerService,
    eligibilityService: EligibilityService,
    documentService: DocumentService
) {
    route("/api/profile") {

        // ── GET /api/profile/digilocker/auth ───────────────────────
        // Generates authorization URL for DigiLocker OAuth
        get("/digilocker/auth") {
            val callbackUrl = call.request.queryParameters["callbackUrl"]
                ?: "https://jansaarthi.gov.in/auth/digilocker/callback"

            val authResponse = digiLockerService.initiateAuth(callbackUrl)
            call.respond(HttpStatusCode.OK, authResponse)
        }

        // ── POST /api/profile/digilocker/callback ──────────────────
        // Token exchange endpoint following citizen consent in DigiLocker
        post("/digilocker/callback") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val request = try {
                call.receive<DigiLockerCallbackRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request body. Required: authorizationCode (String), state (String).")
                )
            }

            if (request.authorizationCode.isBlank() || request.state.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("authorizationCode and state cannot be blank.")
                )
            }

            val tokenResponse = digiLockerService.handleCallback(request)
            call.respond(HttpStatusCode.OK, tokenResponse)
        }

        // ── GET /api/profile/digilocker/documents ──────────────────
        // Fetches citizen's authentic government documents from DigiLocker
        get("/digilocker/documents") {
            val authHeader = call.request.headers["Authorization"]
            val tokenFromHeader = authHeader?.removePrefix("Bearer ")?.trim()
            val token = tokenFromHeader ?: call.request.queryParameters["token"]

            if (token.isNullOrBlank()) {
                return@get call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("DigiLocker access token required via Authorization header (Bearer <token>) or ?token= query parameter.")
                )
            }

            val docs = digiLockerService.pullDocuments(token)
            call.respond(HttpStatusCode.OK, docs)
        }

        // ── POST /api/profile/farmer ───────────────────────────────
        // Farmer-specific profile endpoint: land records, PM-KISAN, KCC
        post("/farmer") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val farmerReq = try {
                call.receive<FarmerProfileRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request body for FarmerProfileRequest.")
                )
            }

            if (farmerReq.state.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("state is required for farmer profile.")
                )
            }

            val authHeader = call.request.headers["Authorization"]
            val token = authHeader?.removePrefix("Bearer ")?.trim()
            val baseDocs = if (!token.isNullOrBlank()) {
                digiLockerService.pullDocuments(token)
            } else {
                emptyList()
            }

            val response = farmerService.buildFarmerProfile(farmerReq, baseDocs)
            call.respond(HttpStatusCode.OK, response)
        }

        // ── POST /api/profile/build ────────────────────────────────
        // Build unified profile (manual / digilocker / farmer mode)
        post("/build") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val request = try {
                call.receive<ProfileBuildRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request body for ProfileBuildRequest.")
                )
            }

            val response = profileService.buildProfile(request)
            call.respond(HttpStatusCode.OK, response)
        }

        // ── POST /api/profile/check-eligibility ────────────────────
        // End-to-end endpoint: builds profile (manual/digilocker/farmer),
        // matches schemes with deterministic rule engine, and evaluates document readiness!
        post("/check-eligibility") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val request = try {
                call.receive<ProfileBuildRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request body. Supported modes: 'manual', 'digilocker', 'farmer'.")
                )
            }

            // 1. Build profile from selected mode
            val profileResponse = profileService.buildProfile(request)
            val profile = profileResponse.profile

            // 2. Map VerifiedProfile -> UserProfile for eligibility engine
            val userProfile = UserProfile(
                age = if (profile.age > 0) profile.age else (request.age ?: 25),
                state = if (profile.state.isNotBlank()) profile.state else (request.state ?: "Maharashtra"),
                district = profile.district ?: request.district,
                occupation = profile.occupation ?: request.occupation ?: if (request.mode == "farmer") "farmer" else "general",
                student = profile.isStudent || request.isStudent,
                annualIncome = profile.annualIncome ?: request.annualIncome ?: 150_000L,
                category = profile.category ?: request.category ?: "General"
            )

            // 3. Run multi-condition eligibility engine
            val eligibility = eligibilityService.checkEligibility(userProfile)

            // 4. Extract citizen available documents (verified + self-declared)
            val availableDocNames = (profile.documents.map { it.documentName } + request.availableDocuments)
                .distinct()
                .filter { it.isNotBlank() }

            // 5. Evaluate document readiness across eligible schemes
            val eligibleSchemeIds = eligibility.schemes
                .filter { it.potentiallyEligible }
                .map { it.schemeId }
                .take(15)

            val documentReadiness = if (eligibleSchemeIds.isNotEmpty()) {
                documentService.checkReadinessBulk(eligibleSchemeIds, availableDocNames)
            } else {
                emptyList()
            }

            // 6. Summary metrics
            val verifiedCount = profile.dataSources.count { it.verified }
            val totalFields = profile.dataSources.size

            val summaryMessage = buildString {
                append("Found ${eligibleSchemeIds.size} potentially eligible schemes for ${userProfile.occupation} profile in ${userProfile.state}. ")
                if (totalFields > 0) {
                    append("$verifiedCount of $totalFields data fields verified via government sources. ")
                }
                if (documentReadiness.isNotEmpty()) {
                    val avg = documentReadiness.map { it.readinessPercentage }.average().toInt()
                    append("Average document readiness is $avg%.")
                }
            }

            val fullResult = FullProfileEligibilityResult(
                profile = profile,
                mode = request.mode,
                eligibility = eligibility,
                documentReadiness = documentReadiness,
                verifiedFieldCount = verifiedCount,
                totalFieldCount = totalFields,
                message = summaryMessage
            )

            call.respond(HttpStatusCode.OK, fullResult)
        }
    }
}
