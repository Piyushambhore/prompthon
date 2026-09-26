const cron = require('node-cron');
const { hasContentChanged } = require('./contentHasher');
const { extractSchemeDiff } = require('./aiDiffAgent');
const schemeStore = require('./schemeStore');

class CrawlerPoller {
  constructor() {
    this.cronTask = null;
    this.isPolling = false;
  }

  /**
   * Processes a simulated or real government circular notification
   */
  async processCircularNotification(schemeId, circularTitle, circularContent) {
    const scheme = schemeStore.getScheme(schemeId);
    if (!scheme) {
      throw new Error(`Scheme '${schemeId}' not recognized in store.`);
    }

    const { changed, newHash } = hasContentChanged(circularContent, scheme.lastContentHash);

    // If content has changed or if manually submitted
    if (changed) {
      console.log(`[CrawlerPoller] Hash change detected for scheme: ${schemeId}. Running AI Diff Agent...`);
      const diffResult = await extractSchemeDiff(scheme, circularContent, circularTitle);

      const pendingUpdate = schemeStore.addPendingUpdate({
        schemeId,
        schemeName: scheme.name,
        currentVersion: scheme.version,
        circularTitle,
        rawContentSample: circularContent.substring(0, 300) + '...',
        newContentHash: newHash,
        ...diffResult
      });

      console.log(`[CrawlerPoller] Update queued for human review. Update ID: ${pendingUpdate.updateId}`);
      return {
        status: 'queued_for_review',
        update: pendingUpdate
      };
    } else {
      console.log(`[CrawlerPoller] No content changes detected for ${schemeId}.`);
      return {
        status: 'no_change',
        message: 'Content hash matches baseline.'
      };
    }
  }

  /**
   * Initializes background cron scheduler
   */
  startScheduler(schedule = process.env.POLL_CRON_SCHEDULE || '0 */6 * * *') {
    if (this.cronTask) {
      this.cronTask.stop();
    }

    this.cronTask = cron.schedule(schedule, async () => {
      console.log(`[CrawlerPoller] Running scheduled background check at ${new Date().toISOString()}`);
      // Simulated poll across registered schemes
      // In production, this fetches from myscheme.gov.in / data.gov.in / RSS feeds
    });

    console.log(`[CrawlerPoller] Background scheduler active (${schedule}).`);
  }

  stopScheduler() {
    if (this.cronTask) {
      this.cronTask.stop();
      this.cronTask = null;
    }
  }
}

const crawlerPoller = new CrawlerPoller();
module.exports = crawlerPoller;
