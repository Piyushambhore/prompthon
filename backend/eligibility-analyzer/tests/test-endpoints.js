const assert = require('assert');
const app = require('../src/app');

let server;
const PORT = 5099;
const BASE_URL = `http://localhost:${PORT}`;

async function runTests() {
  console.log('--- Starting Eligibility Analyzer Test Suite ---');
  server = app.listen(PORT);

  try {
    // Test 1: GET /health
    console.log('\n[Test 1] GET /health');
    const healthRes = await fetch(`${BASE_URL}/health`);
    assert.strictEqual(healthRes.status, 200, 'Health check should return 200');
    const healthData = await healthRes.json();
    assert.deepStrictEqual(healthData, { status: 'ok' }, 'Health check should return { status: "ok" }');
    console.log('✔ Passed');

    // Test 2: POST /api/check-eligibility (Valid Request)
    console.log('\n[Test 2] POST /api/check-eligibility (Valid profile)');
    const validProfilePayload = {
      schemeId: 'pm-kisan',
      userProfile: {
        age: 38,
        income: 180000,
        educationLevel: 'Secondary School',
        caste: 'OBC',
        gender: 'male',
        state: 'Maharashtra',
        employmentStatus: 'Farmer',
        additionalDetails: {
          landSizeAcres: 2.5
        }
      }
    };

    const eligRes = await fetch(`${BASE_URL}/api/check-eligibility`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(validProfilePayload)
    });
    assert.strictEqual(eligRes.status, 200, 'Eligibility check should return 200');
    const eligData = await eligRes.json();
    console.log('Eligibility response:', JSON.stringify(eligData, null, 2));

    assert.strictEqual(typeof eligData.schemeId, 'string');
    assert.strictEqual(typeof eligData.eligible, 'boolean');
    assert.strictEqual(typeof eligData.eligibilityScore, 'number');
    assert.ok(Array.isArray(eligData.failedCriteria), 'failedCriteria must be an array');
    assert.ok(Array.isArray(eligData.passedCriteria), 'passedCriteria must be an array');
    assert.strictEqual(typeof eligData.nextSteps, 'string');
    assert.ok(Array.isArray(eligData.requiredDocuments), 'requiredDocuments must be an array');
    console.log('✔ Passed');

    // Test 3: POST /api/check-eligibility (Validation Error - Missing required field)
    console.log('\n[Test 3] POST /api/check-eligibility (Validation 400 on missing age)');
    const invalidProfilePayload = {
      schemeId: 'pm-kisan',
      userProfile: {
        income: 180000,
        educationLevel: 'Secondary School',
        gender: 'male',
        state: 'Maharashtra',
        employmentStatus: 'Farmer'
      }
    };

    const invalidRes = await fetch(`${BASE_URL}/api/check-eligibility`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(invalidProfilePayload)
    });
    assert.strictEqual(invalidRes.status, 400, 'Missing age should return 400');
    const invalidData = await invalidRes.json();
    assert.ok(invalidData.error, 'Error message must be present');
    console.log('✔ Passed (Received expected 400 error):', invalidData.error);

    // Test 4: POST /api/analyze-rejection (Valid Request)
    console.log('\n[Test 4] POST /api/analyze-rejection (Valid rejection letter)');
    const validRejectionPayload = {
      schemeId: 'pm-kisan',
      rejectionLetter: 'Dear applicant, your application for PM-KISAN benefits with Application ID PMK-2026-9812 has been rejected by the District Agriculture Verification Committee due to missing land possession certificate and discrepancy in Aadhaar seeding with your nominated bank account number.',
      userContext: {
        appliedDate: '2026-08-15',
        applicationId: 'PMK-2026-9812'
      }
    };

    const rejectRes = await fetch(`${BASE_URL}/api/analyze-rejection`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(validRejectionPayload)
    });
    assert.strictEqual(rejectRes.status, 200, 'Rejection analyzer should return 200');
    const rejectData = await rejectRes.json();
    console.log('Rejection analysis response:', JSON.stringify(rejectData, null, 2));

    assert.strictEqual(typeof rejectData.rejectionReason, 'string');
    assert.ok(
      ['document_missing', 'ineligible', 'procedural', 'incomplete'].includes(rejectData.reasonCategory),
      `reasonCategory (${rejectData.reasonCategory}) must be one of allowed categories`
    );
    assert.strictEqual(typeof rejectData.whatWentWrong, 'string');
    assert.strictEqual(typeof rejectData.canReapply, 'boolean');
    assert.ok(Array.isArray(rejectData.suggestedCorrections));
    assert.ok(Array.isArray(rejectData.nextSteps));
    assert.ok(Array.isArray(rejectData.alternativeSchemes));
    assert.strictEqual(typeof rejectData.officialHelpline, 'string');
    console.log('✔ Passed');

    // Test 5: POST /api/analyze-rejection (Text too short < 50 chars)
    console.log('\n[Test 5] POST /api/analyze-rejection (Too short text < 50 chars)');
    const shortRejectionPayload = {
      schemeId: 'pm-kisan',
      rejectionLetter: 'Application was rejected.',
      userContext: {
        appliedDate: '2026-08-15',
        applicationId: null
      }
    };

    const shortRes = await fetch(`${BASE_URL}/api/analyze-rejection`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(shortRejectionPayload)
    });
    assert.strictEqual(shortRes.status, 400, 'Letter shorter than 50 chars should return 400');
    const shortData = await shortRes.json();
    assert.ok(shortData.error.includes('between 50 and 5000 characters'));
    console.log('✔ Passed (Received expected 400 error):', shortData.error);

    // Test 6: CORS check
    console.log('\n[Test 6] CORS allowed origin verification');
    const corsAllowedRes = await fetch(`${BASE_URL}/health`, {
      headers: { 'Origin': 'http://localhost:3000' }
    });
    assert.strictEqual(corsAllowedRes.status, 200);
    assert.strictEqual(corsAllowedRes.headers.get('access-control-allow-origin'), 'http://localhost:3000');
    console.log('✔ Passed (Allowed origin correctly returned)');

    console.log('\n========================================');
    console.log('🎉 ALL TEST CASES PASSED SUCCESSFULLY!');
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
