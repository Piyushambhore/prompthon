const rateLimit = require('express-rate-limit');

/**
 * Rate limit translations: Max 50 translations per minute per IP
 */
const translationRateLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute window
  max: 50, // Limit each IP to 50 translation requests per minute
  standardHeaders: true,
  legacyHeaders: false,
  message: {
    error: 'Too many translation requests. Rate limit is 50 requests per minute per IP.'
  },
  statusCode: 429
});

module.exports = {
  translationRateLimiter
};
