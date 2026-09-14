package dev.umairalishah.docmanager.model;

/**
 * Broad classification for incoming/outgoing correspondence and internal
 * documents, mirroring the category taxonomy used by EDMS/correspondence
 * tracking systems in government and enterprise deployments.
 */
public enum DocumentCategory {
    INCOMING_CORRESPONDENCE,
    OUTGOING_CORRESPONDENCE,
    INTERNAL_MEMO,
    CONTRACT,
    INVOICE,
    REPORT,
    POLICY,
    OTHER
}

