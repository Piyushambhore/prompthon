package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.models.EligibilityResponse
import com.jansaarthi.schemes.models.EligibilitySchemeResult
import com.jansaarthi.schemes.models.UserProfile
import com.jansaarthi.schemes.repositories.DocumentRequirement
import com.jansaarthi.schemes.repositories.EligibilityRule
import com.jansaarthi.schemes.repositories.SchemeRepository
import com.jansaarthi.schemes.repositories.SchemeWithRules

/**
 * Rule-based eligibility engine.
 *
 * For every scheme the engine:
 *   1. Reads the user profile.
 *   2. Evaluates each eligibility rule against the profile.
 *   3. Determines whether the user is *potentially* eligible.
 *   4. Collects human-readable reasons for matched rules.
 *   5. Identifies missing required documents.
 *
 * ⚠️  This engine NEVER claims official approval.  Final eligibility
 *     decisions belong to the relevant government authority.
 */
class EligibilityService(private val repository: SchemeRepository) {

    /** Documents most applicants already possess. */
    private val commonlyAvailableDocuments = setOf(
        "Aadhaar Card",
        "PAN Card",
        "Voter ID",
        "Passport Size Photograph"
    )

    fun checkEligibility(profile: UserProfile): EligibilityResponse {
        val allSchemes = repository.findSchemesWithRules()

        val matched = allSchemes
            .map { evaluate(it, profile) }
            .filter { it.potentiallyEligible }

        return EligibilityResponse(schemes = matched)
    }

    // ── Per-scheme evaluation ──────────────────────────────────────
    private fun evaluate(scheme: SchemeWithRules, profile: UserProfile): EligibilitySchemeResult {
        val reasons = mutableListOf<String>()
        var allPassed = true

        for (rule in scheme.rules) {
            val result = evaluateRule(rule, profile)
            if (result.passed) {
                reasons.add(result.reason)
            } else {
                allPassed = false
            }
        }

        val missing = if (allPassed) {
            determineMissingDocuments(scheme.requiredDocuments)
        } else {
            emptyList()
        }

        return EligibilitySchemeResult(
            schemeId            = scheme.schemeId,
            schemeName          = scheme.schemeName,
            department          = scheme.department,
            potentiallyEligible = allPassed,
            reasons             = if (allPassed) reasons else emptyList(),
            missingDocuments    = missing,
            officialUrl         = scheme.officialUrl
        )
    }

    // ── Rule dispatcher ────────────────────────────────────────────
    private data class RuleResult(val passed: Boolean, val reason: String)

    private fun evaluateRule(rule: EligibilityRule, profile: UserProfile): RuleResult =
        when (rule.field) {
            "age"          -> evaluateAge(rule, profile.age)
            "annualIncome" -> evaluateIncome(rule, profile.annualIncome)
            "state"        -> evaluateState(rule, profile.state)
            "occupation"   -> evaluateOccupation(rule, profile.occupation)
            "student"      -> evaluateStudent(rule, profile.student)
            "category"     -> evaluateCategory(rule, profile.category)
            else           -> RuleResult(true, "Rule field '${rule.field}' was not evaluated")
        }

    // ── Individual evaluators ──────────────────────────────────────

    private fun evaluateAge(rule: EligibilityRule, age: Int): RuleResult {
        return when (rule.operator) {
            "gte" -> {
                val min = rule.value.toIntOrNull() ?: return fail("Invalid age rule configuration")
                RuleResult(age >= min, "Applicant age ($age) meets minimum age requirement of $min years")
            }
            "lte" -> {
                val max = rule.value.toIntOrNull() ?: return fail("Invalid age rule configuration")
                RuleResult(age <= max, "Applicant age ($age) is within the maximum age limit of $max years")
            }
            "between" -> {
                val parts = rule.value.split(",").map { it.trim() }
                if (parts.size != 2) return fail("Invalid age range configuration")
                val lo = parts[0].toIntOrNull() ?: return fail("Invalid age range")
                val hi = parts[1].toIntOrNull() ?: return fail("Invalid age range")
                RuleResult(age in lo..hi, "Applicant age ($age) is within the applicable age range of $lo–$hi years")
            }
            else -> fail("Unknown age operator: ${rule.operator}")
        }
    }

    private fun evaluateIncome(rule: EligibilityRule, income: Long): RuleResult {
        return when (rule.operator) {
            "lte" -> {
                val cap = rule.value.toLongOrNull() ?: return fail("Invalid income rule configuration")
                RuleResult(income <= cap, "Applicant is within the applicable income range (annual income ₹$income ≤ ₹$cap)")
            }
            "gte" -> {
                val floor = rule.value.toLongOrNull() ?: return fail("Invalid income rule configuration")
                RuleResult(income >= floor, "Applicant meets minimum income requirement (annual income ₹$income ≥ ₹$floor)")
            }
            else -> fail("Unknown income operator: ${rule.operator}")
        }
    }

    private fun evaluateState(rule: EligibilityRule, state: String): RuleResult {
        return when (rule.operator) {
            "eq" -> {
                if (rule.value.equals("ALL", ignoreCase = true)) {
                    RuleResult(true, "Scheme is available across all states and union territories")
                } else {
                    val matched = rule.value.equals(state, ignoreCase = true)
                    RuleResult(matched, if (matched) "Applicant belongs to the applicable state ($state)" else "Scheme is limited to ${rule.value}")
                }
            }
            "in" -> {
                val states = rule.value.split(",").map { it.trim() }
                val matched = states.any { it.equals(state, ignoreCase = true) }
                RuleResult(matched, if (matched) "Applicant belongs to the applicable state ($state)" else "Scheme is limited to: ${states.joinToString(", ")}")
            }
            else -> fail("Unknown state operator: ${rule.operator}")
        }
    }

    private fun evaluateOccupation(rule: EligibilityRule, occupation: String): RuleResult {
        return when (rule.operator) {
            "eq" -> {
                val matched = rule.value.equals(occupation, ignoreCase = true)
                RuleResult(matched, if (matched) "Applicant's occupation ($occupation) matches scheme requirement" else "Occupation mismatch")
            }
            "in" -> {
                val occupations = rule.value.split(",").map { it.trim() }
                val matched = occupations.any { it.equals(occupation, ignoreCase = true) }
                RuleResult(matched, if (matched) "Applicant's occupation ($occupation) matches scheme requirement" else "Occupation mismatch")
            }
            "neq" -> {
                val matched = !rule.value.equals(occupation, ignoreCase = true)
                RuleResult(matched, if (matched) "Applicant's occupation qualifies for this scheme" else "Occupation not eligible")
            }
            else -> fail("Unknown occupation operator: ${rule.operator}")
        }
    }

    private fun evaluateStudent(rule: EligibilityRule, isStudent: Boolean): RuleResult {
        val required = rule.value.toBooleanStrictOrNull() ?: return fail("Invalid student rule configuration")
        val matched = isStudent == required
        return when {
            matched && isStudent  -> RuleResult(true, "Applicant is a student")
            matched && !isStudent -> RuleResult(true, "Applicant is not a student (as required)")
            else                  -> RuleResult(false, "Student status mismatch")
        }
    }

    private fun evaluateCategory(rule: EligibilityRule, category: String): RuleResult {
        return when (rule.operator) {
            "eq" -> {
                if (rule.value.equals("ALL", ignoreCase = true)) {
                    RuleResult(true, "Scheme is open to all categories")
                } else {
                    val matched = rule.value.equals(category, ignoreCase = true)
                    RuleResult(matched, if (matched) "Applicant belongs to the applicable category ($category)" else "Category requirement not met")
                }
            }
            "in" -> {
                val categories = rule.value.split(",").map { it.trim() }
                if (categories.any { it.equals("ALL", ignoreCase = true) }) {
                    RuleResult(true, "Scheme is open to all categories")
                } else {
                    val matched = categories.any { it.equals(category, ignoreCase = true) }
                    RuleResult(matched, if (matched) "Applicant belongs to the applicable category ($category)" else "Category requirement not met")
                }
            }
            else -> fail("Unknown category operator: ${rule.operator}")
        }
    }

    // ── Document analysis ──────────────────────────────────────────

    private fun determineMissingDocuments(documents: List<DocumentRequirement>): List<String> =
        documents
            .filter { it.mandatory }
            .map { it.documentName }
            .filter { it !in commonlyAvailableDocuments }

    private fun fail(reason: String) = RuleResult(false, reason)
}
