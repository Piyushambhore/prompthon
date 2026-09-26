package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.models.ExplainResponse

/**
 * Service for explaining official government notices in simple language.
 *
 * Current implementation uses rule-based text processing.  The
 * architecture is ready for a future AI integration:
 *
 *   1. If [aiApiKey] is set, attempts an AI-based explanation first.
 *   2. If the AI call fails (or the key is absent), falls back to
 *      the deterministic rule-based analyser.
 *
 * Contract for AI integration (when implemented):
 *   • Receive the supplied official text only.
 *   • Explain only information supported by that text.
 *   • Never invent eligibility criteria, deadlines, or procedures.
 *   • Return structured JSON validated before forwarding.
 */
class ExplanationService(private val aiApiKey: String?) {

    companion object {
        private const val WARNING =
            "This explanation is based only on the provided official notice. " +
            "Please verify with the issuing authority for confirmation."

        private val ACTION_KEYWORDS = listOf(
            "submit", "apply", "register", "visit", "contact", "download",
            "upload", "fill", "bring", "carry", "report", "attend",
            "complete", "provide", "furnish", "present", "obtain"
        )

        private val REASON_KEYWORDS = listOf(
            "because", "due to", "as per", "in accordance", "pursuant to",
            "on account of", "reason", "therefore", "hence", "since",
            "whereas", "considering", "in view of"
        )
    }

    fun explain(text: String): ExplainResponse {
        // ── Try AI first (if configured) ───────────────────────────
        if (!aiApiKey.isNullOrBlank()) {
            try {
                return explainWithAI(text)
            } catch (e: Exception) {
                // Safe fallback — log without exposing internals
                println("AI explanation unavailable, using rule-based fallback: ${e.message}")
            }
        }

        // ── Deterministic fallback ─────────────────────────────────
        return explainWithRules(text)
    }

    // ── Rule-based analyser ────────────────────────────────────────

    private fun explainWithRules(text: String): ExplainResponse {
        val sentences = splitIntoSentences(text)

        return ExplainResponse(
            summary   = generateSummary(sentences),
            reason    = extractReason(sentences),
            nextSteps = extractNextSteps(sentences),
            warning   = WARNING
        )
    }

    private fun splitIntoSentences(text: String): List<String> =
        text.split(Regex("[.!?]+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

    private fun generateSummary(sentences: List<String>): String {
        if (sentences.isEmpty()) return "No content to summarise."
        return sentences.take(3).joinToString(". ") + "."
    }

    private fun extractReason(sentences: List<String>): String {
        val match = sentences.firstOrNull { sentence ->
            REASON_KEYWORDS.any { kw -> sentence.contains(kw, ignoreCase = true) }
        }
        return match?.trim()
            ?: "No specific reason explicitly stated in the notice."
    }

    private fun extractNextSteps(sentences: List<String>): List<String> {
        val steps = sentences
            .filter { sentence ->
                ACTION_KEYWORDS.any { kw -> sentence.contains(kw, ignoreCase = true) }
            }
            .map { it.trim() }
            .take(5)

        return steps.ifEmpty {
            listOf("Review the complete notice for any required actions.")
        }
    }

    // ── AI stub (ready for future integration) ─────────────────────

    @Suppress("UNUSED_PARAMETER")
    private fun explainWithAI(text: String): ExplainResponse {
        // When implemented, this method should:
        //   1. POST the text to an AI endpoint.
        //   2. Parse and validate the structured JSON response.
        //   3. Ensure nothing is invented beyond the supplied text.
        //   4. Return the validated ExplainResponse.
        throw UnsupportedOperationException("AI integration not yet implemented — falling back to rule-based analysis")
    }
}
