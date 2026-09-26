package com.jansaarthi.schemes.routes

import com.jansaarthi.schemes.models.*
import com.jansaarthi.schemes.services.DocumentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/** Maximum allowed request body size (1 MB). */
private const val MAX_BODY_BYTES = 1_048_576L

fun Route.documentRoutes(documentService: DocumentService) {
    route("/api/documents") {

        // ── POST /api/documents/check-readiness ────────────────────
        post("/check-readiness") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val request = try {
                call.receive<DocumentCheckRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request. Required: schemeId (String), availableDocuments (List).")
                )
            }

            if (request.schemeId.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("schemeId is required and cannot be blank.")
                )
            }

            val result = documentService.checkReadiness(request.schemeId, request.availableDocuments)
                ?: return@post call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Scheme not found: ${request.schemeId}")
                )

            call.respond(HttpStatusCode.OK, result)
        }

        // ── POST /api/documents/check-readiness/bulk ───────────────
        post("/check-readiness/bulk") {
            if ((call.request.contentLength() ?: 0) > MAX_BODY_BYTES) {
                return@post call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    ErrorResponse("Request body too large. Maximum size is 1 MB.")
                )
            }

            val request = try {
                call.receive<BulkDocumentCheckRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request. Required: availableDocuments (List), schemeIds (List).")
                )
            }

            if (request.schemeIds.isEmpty()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("At least one schemeId is required.")
                )
            }

            if (request.schemeIds.size > 50) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Maximum 50 schemes per bulk request.")
                )
            }

            val results = documentService.checkReadinessBulk(request.schemeIds, request.availableDocuments)
            call.respond(HttpStatusCode.OK, results)
        }

        // ── GET /api/documents/required/{schemeId} ─────────────────
        get("/required/{schemeId}") {
            val schemeId = call.parameters["schemeId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Scheme ID is required.")
                )

            val result = documentService.getRequiredDocuments(schemeId)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Scheme not found: $schemeId")
                )

            call.respond(HttpStatusCode.OK, result)
        }
    }
}
