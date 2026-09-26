/**
 * AI Diff Agent: Extracts policy rule changes from government circulars
 */

/**
 * Deterministic / rule-based fallback diff extractor
 */
function extractDiffFallback(existingScheme, circularText, circularTitle = 'Ministry Circular') {
  const text = circularText.toLowerCase();
  const changes = {
    incomeCeiling: { old: existingScheme.criteria?.maxIncome || 300000, new: existingScheme.criteria?.maxIncome || 300000, changed: false },
    ageLimit: { old: '18 years', new: '18 years', changed: false },
    addedCriteria: [],
    removedCriteria: [],
    addedDocuments: [],
    removedDocuments: []
  };

  let summary = 'Routine notification or guideline clarification.';

  // Check for income revisions
  const incomeMatch = circularText.match(/(?:income|ceiling|limit)[\s\S]*?(?:raised|revised|increased|to|at)\s*(?:₹|rs\.?|inr)?\s*([0-9,]{4,})/i);
  if (incomeMatch) {
    const rawNum = incomeMatch[1].replace(/,/g, '');
    const newIncome = parseInt(rawNum, 10);
    if (!isNaN(newIncome) && newIncome > 50000) {
      changes.incomeCeiling.new = newIncome;
      changes.incomeCeiling.changed = true;
      summary = `Income ceiling revised to ₹${newIncome.toLocaleString('en-IN')}.`;
    }
  }

  // Check for newly mandated documents
  if (text.includes('mandatory') || text.includes('compulsory') || text.includes('required to submit')) {
    if (text.includes('ekyc') || text.includes('e-kyc') || text.includes('biometric')) {
      changes.addedDocuments.push('Aadhaar Biometric e-KYC Verification');
      summary += ' Mandatory e-KYC authentication added.';
    }
    if (text.includes('kisan credit card') || text.includes('kcc')) {
      changes.addedDocuments.push('Kisan Credit Card (KCC) Certificate');
    }
  }

  return {
    schemeId: existingScheme.schemeId,
    updateTitle: circularTitle,
    effectiveDate: new Date().toISOString().split('T')[0],
    hasChanges: changes.incomeCeiling.changed || changes.addedDocuments.length > 0 || changes.addedCriteria.length > 0,
    changes,
    summaryOfChanges: summary,
    confidence: 88,
    extractionMethod: 'rule-based-engine'
  };
}

/**
 * Calls OpenAI to extract policy diffs
 */
async function callOpenAIDiff(existingScheme, circularText, circularTitle, apiKey) {
  const model = process.env.OPENAI_MODEL || 'gpt-4o-mini';

  const systemPrompt = `You are a Senior Public Policy Analyst for the Government Scheme Guidance Platform (GSGP).
Compare the existing scheme criteria with the new official government circular.
Extract exact differences into a structured JSON diff.
Return ONLY valid JSON matching this schema:
{
  "updateTitle": "string (concise headline of circular)",
  "effectiveDate": "YYYY-MM-DD",
  "hasChanges": boolean,
  "changes": {
    "incomeCeiling": {
      "old": number or null,
      "new": number or null,
      "changed": boolean
    },
    "ageLimit": {
      "old": string or null,
      "new": string or null,
      "changed": boolean
    },
    "addedCriteria": ["string"],
    "removedCriteria": ["string"],
    "addedDocuments": ["string"],
    "removedDocuments": ["string"]
  },
  "summaryOfChanges": "string (simple 2-sentence summary in plain English)",
  "confidence": number (between 80 and 99)
}`;

  const userPrompt = `Existing Scheme:
${JSON.stringify(existingScheme, null, 2)}

New Government Circular ("${circularTitle}"):
"""
${circularText}
"""

Extract all parameter revisions, newly mandated documents, or modified eligibility ceilings accurately.`;

  const response = await fetch('https://api.openai.com/v1/chat/completions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`
    },
    body: JSON.stringify({
      model,
      messages: [
        { role: 'system', content: systemPrompt },
        { role: 'user', content: userPrompt }
      ],
      temperature: 0.1,
      response_format: { type: 'json_object' }
    })
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`OpenAI diff extraction error: ${errorText}`);
  }

  const data = await response.json();
  const parsed = JSON.parse(data.choices?.[0]?.message?.content);
  return {
    schemeId: existingScheme.schemeId,
    updateTitle: parsed.updateTitle || circularTitle,
    effectiveDate: parsed.effectiveDate || new Date().toISOString().split('T')[0],
    hasChanges: Boolean(parsed.hasChanges),
    changes: parsed.changes || {},
    summaryOfChanges: parsed.summaryOfChanges || 'Policy circular processed.',
    confidence: Number(parsed.confidence) || 90,
    extractionMethod: 'llm-agent'
  };
}

/**
 * Analyzes circular and extracts structured diff
 */
async function extractSchemeDiff(existingScheme, circularText, circularTitle = 'Official Notification') {
  const apiKey = process.env.OPENAI_API_KEY;

  if (apiKey && !apiKey.startsWith('your_')) {
    try {
      return await callOpenAIDiff(existingScheme, circularText, circularTitle, apiKey);
    } catch (err) {
      console.warn(`[AI Diff Agent] LLM call failed: ${err.message}. Falling back to deterministic extractor.`);
    }
  }

  return extractDiffFallback(existingScheme, circularText, circularTitle);
}

module.exports = {
  extractSchemeDiff,
  extractDiffFallback
};
