const rateLimit = require('express-rate-limit');

/**
 * Rate limit LLM calls: max 10 per minute per IP to avoid bill spikes
 */
const llmRateLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 10, // Limit each IP to 10 requests per windowMs
  standardHeaders: true, // Return rate limit info in `RateLimit-*` headers
  legacyHeaders: false, // Disable `X-RateLimit-*` headers
  message: {
    error: 'Too many requests. LLM evaluations are rate-limited to 10 per minute per IP to prevent service overload.'
  },
  statusCode: 429
});

module.exports = {
  llmRateLimiter
};
