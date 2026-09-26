/**
 * Request timeout middleware ensuring requests complete within the specified timeout (default 15s)
 */
function requestTimeout(timeoutMs = 15000) {
  return (req, res, next) => {
    const timer = setTimeout(() => {
      if (!res.headersSent) {
        res.status(504).json({
          error: `Request timed out after ${timeoutMs / 1000} seconds.`
        });
      }
    }, timeoutMs);

    res.on('finish', () => clearTimeout(timer));
    res.on('close', () => clearTimeout(timer));

    next();
  };
}

module.exports = {
  requestTimeout
};
