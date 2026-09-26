const NodeCache = require('node-cache');
const { getSchemeDocConfig, normalizeSchemeId } = require('./schemeDatabase');

// In-memory cache for generated checklists (1 hour TTL)
const checklistCache = new NodeCache({ stdTTL: 3600, checkperiod: 600 });

/**
 * Calls OpenAI API for contextual document acquisition refinement
 */
async function callOpenAIEnhance(doc, schemeName, status, language, apiKey) {
  const model = process.env.OPENAI_MODEL || 'gpt-4o-mini';

  const systemPrompt = `You are an expert citizen assistance documentation specialist for Indian government welfare schemes.
Refine the acquisition instructions for this specific document for scheme: "${schemeName}".
Applicant eligibility status: ${status}.
Language requested: ${language}.
Provide simple, jargon-free, actionable steps that any ordinary citizen can follow.
Return ONLY valid JSON matching this schema:
{
  "stepByStepGuide": ["Step 1: ...", "Step 2: ...", "Step 3: ..."],
  "commonMistakes": ["string"],
  "alternateDocuments": ["string"]
}`;

  const userPrompt = `Document: ${doc.documentName}
Purpose: ${doc.purpose}
Issuing Authority: ${doc.issuingAuthority}
Default steps: ${JSON.stringify(doc.stepByStepGuide)}
Common mistakes: ${JSON.stringify(doc.commonMistakes)}`;

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
      temperature: 0.2,
      response_format: { type: 'json_object' }
    })
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`OpenAI enhancement error: ${errorText}`);
  }

  const data = await response.json();
  return JSON.parse(data.choices?.[0]?.message?.content);
}

/**
 * Generates structured document checklist
 */
async function generateChecklist(schemeId, userEligibilityStatus, language = 'en') {
  const normSchemeId = normalizeSchemeId(schemeId);
  const cacheKey = `checklist_${normSchemeId}_${userEligibilityStatus}_${language}`;

  // 1. Check cache first
  const cachedResult = checklistCache.get(cacheKey);
  if (cachedResult) {
    return cachedResult;
  }

  // 2. Fetch scheme from database
  const schemeConfig = getSchemeDocConfig(normSchemeId);
  if (!schemeConfig) {
    throw new Error(`Scheme '${schemeId}' not found in database.`);
  }

  // 3. Clone and sort documents by priority (most commonly rejected first -> lowest numerical priority number first)
  const sortedDocs = [...schemeConfig.documents].sort((a, b) => {
    const prioA = a.rejectionRiskPriority || 99;
    const prioB = b.rejectionRiskPriority || 99;
    return prioA - prioB;
  });

  const apiKey = process.env.OPENAI_API_KEY || process.env.CLAUDE_API_KEY;
  const canUseLLM = apiKey && !apiKey.startsWith('your_');

  // Process documents with optional LLM enhancement
  const processedDocs = await Promise.all(
    sortedDocs.map(async (doc) => {
      let stepByStepGuide = doc.stepByStepGuide;
      let commonMistakes = doc.commonMistakes;
      let alternateDocuments = doc.alternateDocuments;

      if (canUseLLM) {
        try {
          const enhanced = await callOpenAIEnhance(doc, schemeConfig.schemeName, userEligibilityStatus, language, apiKey);
          if (enhanced.stepByStepGuide && Array.isArray(enhanced.stepByStepGuide)) {
            stepByStepGuide = enhanced.stepByStepGuide;
          }
          if (enhanced.commonMistakes && Array.isArray(enhanced.commonMistakes)) {
            commonMistakes = enhanced.commonMistakes;
          }
          if (enhanced.alternateDocuments && Array.isArray(enhanced.alternateDocuments)) {
            alternateDocuments = enhanced.alternateDocuments;
          }
        } catch (err) {
          console.warn(`[Document Service] LLM enhancement failed for doc ${doc.documentId}: ${err.message}. Using verified database default.`);
        }
      }

      return {
        documentId: doc.documentId,
        documentName: doc.documentName,
        purpose: doc.purpose,
        format: doc.format,
        maxFileSize: doc.maxFileSize,
        acceptedFormats: doc.acceptedFormats,
        issuingAuthority: doc.issuingAuthority,
        validityPeriod: doc.validityPeriod,
        stepByStepGuide,
        commonMistakes,
        alternateDocuments,
        exampleImageUrl: doc.exampleImageUrl
      };
    })
  );

  const responsePayload = {
    schemeId: schemeConfig.schemeId,
    totalDocumentsNeeded: processedDocs.length,
    estimatedCompletionTime: schemeConfig.estimatedCompletionTime || '2-3 days',
    documents: processedDocs,
    submissionProcess: {
      mode: schemeConfig.submissionProcess.mode,
      steps: schemeConfig.submissionProcess.steps,
      estimatedProcessingTime: schemeConfig.submissionProcess.estimatedProcessingTime
    },
    helplineForDocumentation: schemeConfig.helplineForDocumentation
  };

  // Cache template
  checklistCache.set(cacheKey, responsePayload);

  return responsePayload;
}

module.exports = {
  generateChecklist,
  checklistCache
};
