const crypto = require('crypto');

/**
 * In-memory repository of active schemes and pending review updates
 */
class SchemeStore {
  constructor() {
    this.schemes = {
      'pm-kisan': {
        schemeId: 'pm-kisan',
        name: 'Pradhan Mantri Kisan Samman Nidhi',
        version: 1,
        lastUpdated: new Date().toISOString(),
        lastContentHash: 'a1b2c3d4e5f6',
        criteria: {
          maxIncome: 300000,
          minAge: 18,
          landRequired: true,
          exclusions: ['Institutional landholders', 'Income tax payers', 'Serving govt employees']
        },
        requiredDocuments: [
          'Aadhaar Card',
          'Landholding Ownership Documents (RoR / Jamabandi)',
          'Aadhaar-Seeded Bank Passbook'
        ]
      },
      'pmay': {
        schemeId: 'pmay',
        name: 'Pradhan Mantri Awas Yojana',
        version: 1,
        lastUpdated: new Date().toISOString(),
        lastContentHash: 'f6e5d4c3b2a1',
        criteria: {
          maxIncomeEWS: 300000,
          maxIncomeLIG: 600000,
          minAge: 18,
          noPuccaHouseRequired: true
        },
        requiredDocuments: [
          'Aadhaar Card of all family members',
          'Income Certificate',
          'Affidavit proving no pucca house ownership'
        ]
      }
    };

    // Queue of pending updates awaiting human-in-the-loop review
    this.pendingUpdates = [];
    this.auditLog = [];
  }

  getScheme(schemeId) {
    return this.schemes[schemeId] || null;
  }

  getAllSchemes() {
    return Object.values(this.schemes);
  }

  addPendingUpdate(update) {
    const updateId = `upd_${Date.now()}_${crypto.randomBytes(4).toString('hex')}`;
    const pendingItem = {
      updateId,
      status: 'pending_review',
      createdAt: new Date().toISOString(),
      ...update
    };
    this.pendingUpdates.unshift(pendingItem);
    return pendingItem;
  }

  getPendingUpdates() {
    return this.pendingUpdates.filter(u => u.status === 'pending_review');
  }

  getUpdateById(updateId) {
    return this.pendingUpdates.find(u => u.updateId === updateId);
  }

  approveUpdate(updateId, reviewerName = 'Platform Admin') {
    const update = this.getUpdateById(updateId);
    if (!update) {
      throw new Error(`Update '${updateId}' not found.`);
    }

    if (update.status !== 'pending_review') {
      throw new Error(`Update '${updateId}' is already ${update.status}.`);
    }

    const scheme = this.schemes[update.schemeId];
    if (!scheme) {
      throw new Error(`Target scheme '${update.schemeId}' does not exist.`);
    }

    // Apply approved changes to live scheme
    scheme.version += 1;
    scheme.lastUpdated = new Date().toISOString();
    if (update.newContentHash) {
      scheme.lastContentHash = update.newContentHash;
    }

    // Apply criteria diffs
    if (update.changes) {
      if (update.changes.incomeCeiling && update.changes.incomeCeiling.changed) {
        scheme.criteria.maxIncome = update.changes.incomeCeiling.new;
      }
      if (update.changes.addedDocuments && update.changes.addedDocuments.length > 0) {
        scheme.requiredDocuments = Array.from(new Set([...scheme.requiredDocuments, ...update.changes.addedDocuments]));
      }
      if (update.changes.removedDocuments && update.changes.removedDocuments.length > 0) {
        scheme.requiredDocuments = scheme.requiredDocuments.filter(d => !update.changes.removedDocuments.includes(d));
      }
      if (update.changes.addedCriteria && update.changes.addedCriteria.length > 0) {
        scheme.criteria.exclusions = Array.from(new Set([...(scheme.criteria.exclusions || []), ...update.changes.addedCriteria]));
      }
    }

    update.status = 'approved';
    update.reviewedBy = reviewerName;
    update.reviewedAt = new Date().toISOString();

    this.auditLog.push({
      action: 'APPROVE_SCHEME_UPDATE',
      schemeId: scheme.schemeId,
      newVersion: scheme.version,
      updateId,
      timestamp: new Date().toISOString(),
      reviewer: reviewerName
    });

    return {
      success: true,
      scheme,
      update
    };
  }

  rejectUpdate(updateId, reason = 'False positive or rejected by admin', reviewerName = 'Platform Admin') {
    const update = this.getUpdateById(updateId);
    if (!update) {
      throw new Error(`Update '${updateId}' not found.`);
    }

    update.status = 'rejected';
    update.rejectionReason = reason;
    update.reviewedBy = reviewerName;
    update.reviewedAt = new Date().toISOString();

    this.auditLog.push({
      action: 'REJECT_SCHEME_UPDATE',
      schemeId: update.schemeId,
      updateId,
      reason,
      timestamp: new Date().toISOString(),
      reviewer: reviewerName
    });

    return {
      success: true,
      update
    };
  }
}

const schemeStore = new SchemeStore();
module.exports = schemeStore;
