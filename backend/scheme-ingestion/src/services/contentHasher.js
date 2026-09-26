const crypto = require('crypto');

/**
 * Normalizes text content by removing volatile whitespace, timestamps, and formatting
 */
function normalizeContent(content) {
  if (typeof content !== 'string') {
    content = JSON.stringify(content || '');
  }
  return content
    .replace(/\s+/g, ' ')
    .trim()
    .toLowerCase();
}

/**
 * Computes SHA-256 hash of normalized content
 */
function computeHash(content) {
  const normalized = normalizeContent(content);
  return crypto.createHash('sha256').update(normalized).digest('hex');
}

/**
 * Checks if new content differs from previous hash
 */
function hasContentChanged(newContent, previousHash) {
  const newHash = computeHash(newContent);
  return {
    changed: newHash !== previousHash,
    newHash,
    previousHash
  };
}

module.exports = {
  normalizeContent,
  computeHash,
  hasContentChanged
};
