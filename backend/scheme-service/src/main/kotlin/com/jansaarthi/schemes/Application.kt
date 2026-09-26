package com.jansaarthi.schemes

import com.jansaarthi.schemes.ai.AIProvider
import com.jansaarthi.schemes.ai.OpenAIProvider
import com.jansaarthi.schemes.ai.RuleBasedProvider
import com.jansaarthi.schemes.config.DatabaseConfig
import com.jansaarthi.schemes.config.DatabaseSeeder
import com.jansaarthi.schemes.models.ErrorResponse
import com.jansaarthi.schemes.repositories.DocumentRepository
import com.jansaarthi.schemes.repositories.SchemeRepository
import com.jansaarthi.schemes.routes.documentRoutes
import com.jansaarthi.schemes.routes.healthRoutes
import com.jansaarthi.schemes.routes.profileRoutes
import com.jansaarthi.schemes.routes.schemeRoutes
import com.jansaarthi.schemes.services.DigiLockerService
import com.jansaarthi.schemes.services.DocumentService
import com.jansaarthi.schemes.services.EligibilityService
import com.jansaarthi.schemes.services.ExplanationService
import com.jansaarthi.schemes.services.FarmerService
import com.jansaarthi.schemes.services.ProfileService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    // ── Load environment ───────────────────────────────────────────
    val env = dotenv {
        ignoreIfMissing = true
    }

    val port       = env["PORT"]?.toIntOrNull() ?: 8080
    val dbUrl      = env["DATABASE_URL"]      ?: "jdbc:postgresql://localhost:5432/jansaarthi"
    val dbUser     = env["DATABASE_USER"]      ?: "postgres"
    val dbPassword = env["DATABASE_PASSWORD"]  ?: "postgres"
    val aiApiKey   = env["AI_API_KEY"]

    // ── Initialise database & seed demo data ───────────────────────
    DatabaseConfig.init(dbUrl, dbUser, dbPassword)
    DatabaseSeeder.seed()

    // ── Repositories ───────────────────────────────────────────────
    val schemeRepository   = SchemeRepository()
    val documentRepository = DocumentRepository()

    // ── AI provider chain (order matters — first available wins) ──
    val providers: List<AIProvider> = listOf(
        OpenAIProvider(aiApiKey),    // tries AI first (if key is set)
        RuleBasedProvider()          // guaranteed fallback
    )

    // ── Services ───────────────────────────────────────────────────
    val eligibilityService  = EligibilityService(schemeRepository)
    val documentService     = DocumentService(documentRepository)
    val explanationService  = ExplanationService(providers)
    val digiLockerService   = DigiLockerService()
    val farmerService       = FarmerService()
    val profileService      = ProfileService(digiLockerService, farmerService)

    // ── Start Ktor ─────────────────────────────────────────────────
    embeddedServer(Netty, port = port) {
        configurePlugins()
        configureRouting(
            schemeRepository,
            eligibilityService,
            documentService,
            explanationService,
            profileService,
            digiLockerService,
            farmerService
        )
    }.start(wait = true)
}

// ── Plugins ────────────────────────────────────────────────────────

fun Application.configurePlugins() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint       = true
            isLenient         = true
            ignoreUnknownKeys = true
        })
    }

    install(CORS) {
        allowHost("localhost:3000")                // Dev frontend
        allowHost("localhost:5001")                // Eligibility analyzer micro-service
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(cause.message ?: "Invalid request")
            )
        }
        exception<Throwable> { call, cause ->
            // Never expose stack traces, credentials, or internals
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("An unexpected error occurred. Please try again later.")
            )
        }
    }
}

// ── Routing ────────────────────────────────────────────────────────

fun Application.configureRouting(
    schemeRepository: SchemeRepository,
    eligibilityService: EligibilityService,
    documentService: DocumentService,
    explanationService: ExplanationService,
    profileService: ProfileService,
    digiLockerService: DigiLockerService,
    farmerService: FarmerService
) {
    routing {
        healthRoutes()
        schemeRoutes(schemeRepository, eligibilityService, explanationService)
        documentRoutes(documentService)
        profileRoutes(profileService, digiLockerService, farmerService, eligibilityService, documentService)
    }
}
