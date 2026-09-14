package dev.umairalishah.docmanager.dto;

import dev.umairalishah.docmanager.model.DocumentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull(message = "status is required")
        DocumentStatus status
) {
}

