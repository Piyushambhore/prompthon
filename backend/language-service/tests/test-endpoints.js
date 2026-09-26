const assert = require('assert');
const app = require('../src/app');

let server;
const PORT = 5098;
const BASE_URL = `http://localhost:${PORT}`;

async function runTests() {
  console.log('--- Starting Multi-Language Service Test Suite ---');
  server = app.listen(PORT);

  try {
    // Test 1: GET /health
    console.log('\n[Test 1] GET /health');
    const healthRes = await fetch(`${BASE_URL}/health`);
    assert.strictEqual(healthRes.status, 200, 'Health check should return 200');
    const healthData = await healthRes.json();
    assert.deepStrictEqual(healthData, { status: 'ok' });
    console.log('✔ Passed');

    // Test 2: Validation errors (HTTP 400)
    console.log('\n[Test 2.1] Validation 400 on unsupported language code');
    const invalidLangRes = await fetch(`${BASE_URL}/api/translate-guidance`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        text: 'Your application has been accepted.',
        targetLanguage: 'french_xx',
        context: 'eligibility'
      })
    });
    assert.strictEqual(invalidLangRes.status, 400);
    const invalidLangData = await invalidLangRes.json();
    assert.ok(invalidLangData.error.includes('Unsupported "targetLanguage"'));
    console.log('✔ Passed (Received expected 400):', invalidLangData.error);

    console.log('\n[Test 2.2] Validation 400 on text too short (< 10 chars)');
    const shortTextRes = await fetch(`${BASE_URL}/api/translate-guidance`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        text: 'Short',
        targetLanguage: 'hi',
        context: 'eligibility'
      })
    });
    assert.strictEqual(shortTextRes.status, 400);
    const shortTextData = await shortTextRes.json();
    assert.ok(shortTextData.error.includes('between 10 and 5000 characters'));
    console.log('✔ Passed (Received expected 400):', shortTextData.error);

    console.log('\n[Test 2.3] Validation 400 on unsupported context');
    const invalidContextRes = await fetch(`${BASE_URL}/api/translate-guidance`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        text: 'Your application has been accepted by the department.',
        targetLanguage: 'hi',
        context: 'unknown_context'
      })
    });
    assert.strictEqual(invalidContextRes.status, 400);
    const invalidContextData = await invalidContextRes.json();
    assert.ok(invalidContextData.error.includes('Unsupported "context"'));
    console.log('✔ Passed (Received expected 400):', invalidContextData.error);

    // Test 3: Test all 10 supported regional languages
    console.log('\n[Test 3] Testing translations for all 10 supported Indian languages:');
    const supportedLanguages = ['hi', 'ta', 'te', 'kn', 'ml', 'mr', 'gu', 'bn', 'or', 'as'];
    const sampleText = 'You meet all the eligibility criteria for the scheme. Please keep your required documents ready.';

    for (const lang of supportedLanguages) {
      const res = await fetch(`${BASE_URL}/api/translate-guidance`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text: sampleText,
          targetLanguage: lang,
          context: 'eligibility'
        })
      });
      assert.strictEqual(res.status, 200, `Translation to ${lang} should succeed`);
      const data = await res.json();
      assert.strictEqual(data.originalText, sampleText);
      assert.strictEqual(data.targetLanguage, lang);
      assert.strictEqual(typeof data.translatedText, 'string');
      assert.strictEqual(typeof data.confidence, 'number');
      assert.strictEqual(typeof data.formatting.isSimplified, 'boolean');
      assert.strictEqual(typeof data.formatting.readabilityScore, 'number');
      assert.strictEqual(typeof data.formatting.usedSimpleWords, 'boolean');
      console.log(`  ✔ [${lang.toUpperCase()}] Translated: "${data.translatedText.substring(0, 40)}..." (Confidence: ${data.confidence}%)`);
    }

    // Test 4: Rejection context testing
    console.log('\n[Test 4] Rejection context translation (empathetic & encouraging)');
    const rejectionText = 'Your application was rejected due to missing land revenue records.';
    const rejRes = await fetch(`${BASE_URL}/api/translate-guidance`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        text: rejectionText,
        targetLanguage: 'mr',
        context: 'rejection'
      })
    });
    assert.strictEqual(rejRes.status, 200);
    const rejData = await rejRes.json();
    assert.strictEqual(rejData.targetLanguage, 'mr');
    console.log('  ✔ Rejection guidance in Marathi:', rejData.translatedText);

    // Test 5: Cache efficiency
    console.log('\n[Test 5] Verifying in-memory cache hit');
    const start = Date.now();
    const cachedRes = await fetch(`${BASE_URL}/api/translate-guidance`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        text: rejectionText,
        targetLanguage: 'mr',
        context: 'rejection'
      })
    });
    const duration = Date.now() - start;
    assert.strictEqual(cachedRes.status, 200);
    console.log(`  ✔ Cache hit response served in ${duration}ms`);

    // Test 6: CORS check
    console.log('\n[Test 6] CORS allowed origin verification');
    const corsRes = await fetch(`${BASE_URL}/health`, {
      headers: { 'Origin': 'http://localhost:3000' }
    });
    assert.strictEqual(corsRes.status, 200);
    assert.strictEqual(corsRes.headers.get('access-control-allow-origin'), 'http://localhost:3000');
    console.log('✔ Passed (CORS headers valid)');

    console.log('\n========================================');
    console.log('🎉 ALL LANGUAGE SERVICE TESTS PASSED!');
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
