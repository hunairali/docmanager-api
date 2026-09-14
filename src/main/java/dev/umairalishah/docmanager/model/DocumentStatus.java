package dev.umairalishah.docmanager.model;

import java.util.Set;

/**
 * Workflow state for a document. Transitions are intentionally restricted
 * (see {@link #canTransitionTo(DocumentStatus)}) to model the approval
 * workflow that correspondence-tracking systems enforce in production -
 * a document cannot jump from DRAFT straight to ARCHIVED, for example.
 */
public enum DocumentStatus {
    DRAFT,
    PENDING_REVIEW,
    APPROVED,
    REJECTED,
    ARCHIVED;

    private static final Set<DocumentStatus> TERMINAL = Set.of(ARCHIVED);

    public boolean canTransitionTo(DocumentStatus target) {
        if (this == target) {
            return false;
        }
        if (TERMINAL.contains(this)) {
            return false;
        }
        return switch (this) {
            case DRAFT -> target == PENDING_REVIEW;
            case PENDING_REVIEW -> target == APPROVED || target == REJECTED;
            case APPROVED -> target == ARCHIVED;
            case REJECTED -> target == DRAFT || target == ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}

