package com.jansaarthi.schemes.routes

import com.jansaarthi.schemes.models.*
import com.jansaarthi.schemes.repositories.SchemeRepository
import com.jansaarthi.schemes.services.EligibilityService
import com.jansaarthi.schemes.services.ExplanationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/** Maximum allowed request body size (1 MB). */
private const val MAX_BODY_BYTES = 1_048_576L

fun Route.schemeRoutes(
    repository: SchemeRepository,
    eligibilityService: EligibilityService,
    explanationService: ExplanationService
) {
    route("/api") {

        // ── GET /api/schemes ───────────────────────────────────────
        get("/schemes") {
            val state    = call.request.queryParameters["state"]
            val category = call.request.queryParameters["category"]
            val schemes  = repository.findAll(state, category)
            call.respond(HttpStatusCode.OK, schemes)
        }

        // ── GET /api/schemes/{schemeId} ────────────────────────────
        get("/schemes/{schemeId}") {
            val schemeId = call.parameters["schemeId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Scheme ID is required.")
                )

            val scheme = repository.findById(schemeId)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Scheme not found: $schemeId")
                )

            call.respond(HttpStatusCode.OK, scheme)
        }

        // ── POST /api/schemes/check-eligibility ────────────────────
        post("/schemes/check-eligibility") {
            // Guard: reject oversized bodies
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val profile = try {
                call.receive<UserProfile>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        "Invalid request body. Required fields: age (Int), state (String), " +
                        "occupation (String), annualIncome (Long), category (String)."
                    )
                )
            }

            val errors = validateUserProfile(profile)
            if (errors.isNotEmpty()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Validation failed.", errors)
                )
            }

            val result = eligibilityService.checkEligibility(profile)
            call.respond(HttpStatusCode.OK, result)
        }

        // ── POST /api/explain ──────────────────────────────────────
        post("/explain") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val request = try {
                call.receive<ExplainRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request body. Required field: text (String).")
                )
            }

            if (request.text.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Text field cannot be empty.")
                )
            }

            if (request.text.length > 10_000) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Text is too long. Maximum 10,000 characters allowed.")
                )
            }

            val result = explanationService.explain(request.text)
            call.respond(HttpStatusCode.OK, result)
        }
    }
}

// ── Input validation ───────────────────────────────────────────────

private val VALID_CATEGORIES = setOf("GENERAL", "OBC", "SC", "ST", "EWS")

private fun validateUserProfile(profile: UserProfile): List<String> {
    val errors = mutableListOf<String>()

    if (profile.age < 0 || profile.age > 150) {
        errors.add("age must be between 0 and 150.")
    }
    if (profile.state.isBlank()) {
        errors.add("state is required and cannot be blank.")
    }
    if (profile.occupation.isBlank()) {
        errors.add("occupation is required and cannot be blank.")
    }
    if (profile.annualIncome < 0) {
        errors.add("annualIncome cannot be negative.")
    }
    if (profile.annualIncome > 100_000_000L) {
        errors.add("annualIncome seems unreasonably large (max ₹10,00,00,000).")
    }
    if (profile.category.isBlank()) {
        errors.add("category is required and cannot be blank.")
    } else if (profile.category.uppercase() !in VALID_CATEGORIES) {
        errors.add("category must be one of: General, OBC, SC, ST, EWS.")
    }

    return errors
}
