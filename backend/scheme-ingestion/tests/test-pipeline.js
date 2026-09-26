const assert = require('assert');
const app = require('../src/app');

let server;
const PORT = 5096;
const BASE_URL = `http://localhost:${PORT}`;

async function runTests() {
  console.log('--- Starting Hybrid Ingestion Pipeline Test Suite ---');
  server = app.listen(PORT);

  try {
    // Test 1: GET /health
    console.log('\n[Test 1] GET /health');
    const healthRes = await fetch(`${BASE_URL}/health`);
    assert.strictEqual(healthRes.status, 200);
    const healthData = await healthRes.json();
    assert.strictEqual(healthData.status, 'ok');
    console.log('✔ Passed');

    // Test 2: Verify Initial Scheme Baseline
    console.log('\n[Test 2] Verify baseline scheme version and income limit');
    const schemesRes = await fetch(`${BASE_URL}/api/ingestion/schemes`);
    assert.strictEqual(schemesRes.status, 200);
    const schemesData = await schemesRes.json();
    const pmKisan = schemesData.schemes.find(s => s.schemeId === 'pm-kisan');
    assert.strictEqual(pmKisan.version, 1);
    assert.strictEqual(pmKisan.criteria.maxIncome, 300000);
    console.log(`  ✔ PM-KISAN baseline: Version 1, Max Income ₹${pmKisan.criteria.maxIncome}`);

    // Test 3: Ingestion Pipeline - Trigger Poll with New Government Gazette Circular
    console.log('\n[Test 3] Simulating new official ministry gazette circular');
    const circularPayload = {
      schemeId: 'pm-kisan',
      circularTitle: 'MoA Gazette Circular No. 44/2026: PM-KISAN Annual Revision',
      circularContent: `OFFICIAL NOTIFICATION: The Ministry of Agriculture and Farmers Welfare hereby notifies that the annual family income ceiling under the PM-KISAN scheme is revised to ₹3,50,000 effective immediately. Additionally, all beneficiaries are required to submit Aadhaar Biometric e-KYC Verification before the 17th disbursement installment.`
    };

    const pollRes = await fetch(`${BASE_URL}/api/ingestion/trigger-poll`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(circularPayload)
    });
    assert.strictEqual(pollRes.status, 200);
    const pollData = await pollRes.json();
    console.log('Ingestion response:', JSON.stringify(pollData.result, null, 2));

    assert.strictEqual(pollData.result.status, 'queued_for_review');
    const update = pollData.result.update;
    assert.ok(update.updateId);
    assert.strictEqual(update.status, 'pending_review');
    assert.strictEqual(update.changes.incomeCeiling.changed, true);
    assert.strictEqual(update.changes.incomeCeiling.new, 350000);
    assert.ok(update.changes.addedDocuments.includes('Aadhaar Biometric e-KYC Verification'));
    console.log(`  ✔ AI Diff Agent extracted revised income: ₹${update.changes.incomeCeiling.new}`);
    console.log(`  ✔ AI Diff Agent detected mandatory new document: ${update.changes.addedDocuments.join(', ')}`);
    console.log('✔ Passed');

    // Test 4: Query Pending Updates Queue (Human-in-the-Loop Review Dashboard)
    console.log('\n[Test 4] Querying pending updates awaiting human approval');
    const pendingRes = await fetch(`${BASE_URL}/api/ingestion/pending-updates`);
    assert.strictEqual(pendingRes.status, 200);
    const pendingData = await pendingRes.json();
    assert.strictEqual(pendingData.totalPending, 1);
    assert.strictEqual(pendingData.updates[0].updateId, update.updateId);
    console.log(`  ✔ Found 1 pending update in queue awaiting approval: ${pendingData.updates[0].updateTitle}`);
    console.log('✔ Passed');

    // Test 5: 1-Click Human Approval
    console.log('\n[Test 5] Human-in-the-loop 1-Click Approval action');
    const approveRes = await fetch(`${BASE_URL}/api/ingestion/approve/${update.updateId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reviewerName: 'Senior Policy Officer' })
    });
    assert.strictEqual(approveRes.status, 200);
    const approveData = await approveRes.json();
    console.log(`  ✔ Approved! New scheme version: ${approveData.scheme.version}`);
    console.log(`  ✔ Reviewed by: ${approveData.update.reviewedBy}`);
    assert.strictEqual(approveData.scheme.version, 2);
    assert.strictEqual(approveData.scheme.criteria.maxIncome, 350000);
    assert.ok(approveData.scheme.requiredDocuments.includes('Aadhaar Biometric e-KYC Verification'));
    console.log('✔ Passed');

    // Test 6: Verify Live Updated Scheme State & Audit Trail
    console.log('\n[Test 6] Verifying live scheme state and audit log');
    const updatedSchemesRes = await fetch(`${BASE_URL}/api/ingestion/schemes`);
    const updatedSchemesData = await updatedSchemesRes.json();
    const updatedPmKisan = updatedSchemesData.schemes.find(s => s.schemeId === 'pm-kisan');
    assert.strictEqual(updatedPmKisan.version, 2);
    assert.strictEqual(updatedPmKisan.criteria.maxIncome, 350000);

    const auditRes = await fetch(`${BASE_URL}/api/ingestion/audit-log`);
    const auditData = await auditRes.json();
    assert.strictEqual(auditData.auditLog.length, 1);
    assert.strictEqual(auditData.auditLog[0].action, 'APPROVE_SCHEME_UPDATE');
    console.log(`  ✔ Audit record verified: ${auditData.auditLog[0].action} by ${auditData.auditLog[0].reviewer}`);
    console.log('✔ Passed');

    console.log('\n=================================================');
    console.log('🎉 ALL INGESTION PIPELINE TESTS PASSED!');
    console.log('=================================================\n');
  } catch (err) {
    console.error('❌ Ingestion test failed:', err);
    process.exitCode = 1;
  } finally {
    if (server) {
      server.close();
    }
  }
}

runTests();
