package com.jansaarthi.schemes.ai

import com.jansaarthi.schemes.models.ExplainResponse

/**
 * Deterministic, rule-based text analyser that is always available.
 *
 * This provider extracts key information from official notice text
 * using keyword matching — no external API calls required.
 * It serves as the guaranteed fallback when AI providers are
 * unavailable or fail.
 */
class RuleBasedProvider : AIProvider {

    override val name = "RuleBasedProvider"

    override fun isAvailable(): Boolean = true   // always available

    override suspend fun explain(text: String): ExplainResponse {
        val sentences = splitIntoSentences(text)

        return ExplainResponse(
            summary   = generateSummary(sentences),
            reason    = extractReason(sentences),
            nextSteps = extractNextSteps(sentences),
            warning   = WARNING
        )
    }

    // ── Helpers ────────────────────────────────────────────────────

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
}
