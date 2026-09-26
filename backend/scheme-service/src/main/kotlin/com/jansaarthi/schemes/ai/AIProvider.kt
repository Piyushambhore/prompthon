package com.jansaarthi.schemes.ai

import com.jansaarthi.schemes.models.ExplainResponse

/**
 * Abstraction for AI-powered text explanation providers.
 *
 * Implementations:
 *   - [RuleBasedProvider]  — deterministic keyword extraction (always available)
 *   - [OpenAIProvider]     — LLM-based explanation (requires API key)
 *
 * Adding a new provider (e.g. Gemini, Claude) only requires
 * implementing this interface — no changes to [ExplanationService].
 */
interface AIProvider {

    /** Human-readable name used for logging. */
    val name: String

    /** Whether this provider can currently handle requests. */
    fun isAvailable(): Boolean

    /**
     * Explain the supplied official notice text.
     *
     * Contract:
     *   1. Explain ONLY information present in [text].
     *   2. Never invent eligibility criteria, deadlines, or procedures.
     *   3. Return structured [ExplainResponse] with a warning disclaimer.
     *   4. Throw on failure — the caller will try the next provider.
     */
    suspend fun explain(text: String): ExplainResponse
}
