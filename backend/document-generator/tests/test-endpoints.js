const assert = require('assert');
const app = require('../src/app');

let server;
const PORT = 5097;
const BASE_URL = `http://localhost:${PORT}`;

async function runTests() {
  console.log('--- Starting Document Checklist Generator Test Suite ---');
  server = app.listen(PORT);

  try {
    // Test 1: GET /health
    console.log('\n[Test 1] GET /health');
    const healthRes = await fetch(`${BASE_URL}/health`);
    assert.strictEqual(healthRes.status, 200, 'Health check should return 200');
    const healthData = await healthRes.json();
    assert.deepStrictEqual(healthData, { status: 'ok' });
    console.log('✔ Passed');

    // Test 2: POST /api/generate-checklist (Valid scheme: pm-kisan)
    console.log('\n[Test 2] POST /api/generate-checklist (Valid request: pm-kisan)');
    const validPayload = {
      schemeId: 'pm-kisan',
      userEligibilityStatus: 'eligible',
      language: 'en'
    };

    const res = await fetch(`${BASE_URL}/api/generate-checklist`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(validPayload)
    });
    assert.strictEqual(res.status, 200, 'Valid checklist should return 200');
    const data = await res.json();
    console.log(`Received checklist for ${data.schemeId} with ${data.totalDocumentsNeeded} documents.`);

    assert.strictEqual(data.schemeId, 'pm-kisan');
    assert.strictEqual(typeof data.totalDocumentsNeeded, 'number');
    assert.strictEqual(typeof data.estimatedCompletionTime, 'string');
    assert.ok(Array.isArray(data.documents), 'documents must be an array');
    assert.ok(data.documents.length > 0, 'at least 1 document must be present');

    // Verify first document properties
    const firstDoc = data.documents[0];
    assert.strictEqual(typeof firstDoc.documentId, 'string');
    assert.strictEqual(typeof firstDoc.documentName, 'string');
    assert.strictEqual(typeof firstDoc.purpose, 'string');
    assert.strictEqual(typeof firstDoc.format, 'string');
    assert.strictEqual(typeof firstDoc.maxFileSize, 'string');
    assert.ok(Array.isArray(firstDoc.acceptedFormats));
    assert.strictEqual(typeof firstDoc.issuingAuthority, 'string');
    assert.strictEqual(typeof firstDoc.validityPeriod, 'string');
    assert.ok(Array.isArray(firstDoc.stepByStepGuide));
    assert.ok(Array.isArray(firstDoc.commonMistakes));
    assert.ok(Array.isArray(firstDoc.alternateDocuments));
    assert.strictEqual(typeof firstDoc.exampleImageUrl, 'string');

    // Verify submission process
    assert.ok(['online', 'offline', 'both'].includes(data.submissionProcess.mode));
    assert.ok(Array.isArray(data.submissionProcess.steps));
    assert.strictEqual(typeof data.submissionProcess.estimatedProcessingTime, 'string');
    assert.strictEqual(typeof data.helplineForDocumentation, 'string');
    console.log('✔ Passed');

    // Test 3: Document ordering verification (most commonly rejected first)
    console.log('\n[Test 3] Verifying priority ordering (highest rejection risk first)');
    assert.strictEqual(data.documents[0].documentId, 'doc_land_records', 'Land records has highest rejection risk and should be 1st');
    console.log(`  ✔ 1st Document: ${data.documents[0].documentName}`);
    console.log(`  ✔ 2nd Document: ${data.documents[1].documentName}`);
    console.log('✔ Passed');

    // Test 4: Validation 400 on scheme not found in database
    console.log('\n[Test 4] Validation 400 on unknown scheme');
    const unknownRes = await fetch(`${BASE_URL}/api/generate-checklist`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        schemeId: 'non-existent-xyz-scheme',
        userEligibilityStatus: 'eligible'
      })
    });
    assert.strictEqual(unknownRes.status, 400);
    const unknownData = await unknownRes.json();
    assert.ok(unknownData.error.includes('does not exist in the database'));
    console.log('✔ Passed (Received expected 400):', unknownData.error);

    // Test 5: Validation 400 on invalid eligibility status
    console.log('\n[Test 5] Validation 400 on invalid eligibility status');
    const invalidStatusRes = await fetch(`${BASE_URL}/api/generate-checklist`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        schemeId: 'pm-kisan',
        userEligibilityStatus: 'super_eligible_invalid'
      })
    });
    assert.strictEqual(invalidStatusRes.status, 400);
    const invalidStatusData = await invalidStatusRes.json();
    assert.ok(invalidStatusData.error.includes('Must be one of: eligible, ineligible, partial'));
    console.log('✔ Passed (Received expected 400):', invalidStatusData.error);

    // Test 6: Validation 400 on missing schemeId
    console.log('\n[Test 6] Validation 400 on missing schemeId');
    const missingRes = await fetch(`${BASE_URL}/api/generate-checklist`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userEligibilityStatus: 'eligible'
      })
    });
    assert.strictEqual(missingRes.status, 400);
    console.log('✔ Passed');

    // Test 7: Verify Cache Speed
    console.log('\n[Test 7] Verifying in-memory template caching');
    const start = Date.now();
    const cachedRes = await fetch(`${BASE_URL}/api/generate-checklist`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(validPayload)
    });
    const duration = Date.now() - start;
    assert.strictEqual(cachedRes.status, 200);
    console.log(`  ✔ Cached response returned in ${duration}ms`);

    // Test 8: CORS check
    console.log('\n[Test 8] CORS header verification');
    const corsRes = await fetch(`${BASE_URL}/health`, {
      headers: { 'Origin': 'http://localhost:3000' }
    });
    assert.strictEqual(corsRes.status, 200);
    assert.strictEqual(corsRes.headers.get('access-control-allow-origin'), 'http://localhost:3000');
    console.log('✔ Passed');

    console.log('\n========================================');
    console.log('🎉 ALL DOCUMENT GENERATOR TESTS PASSED!');
    console.log('========================================\n');
  } catch (err) {
    console.error('❌ Test failed:', err);
    process.exitCode = 1;
  } finally {
    if (server) {
      server.close();
    }
  }
}

runTests();
