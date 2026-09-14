package dev.umairalishah.docmanager.dto;

import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.model.DocumentStatus;

import java.time.Instant;
import java.time.LocalDate;

public record DocumentResponse(
        Long id,
        String referenceNumber,
        String title,
        String description,
        DocumentCategory category,
        DocumentStatus status,
        String createdBy,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getReferenceNumber(),
                document.getTitle(),
                document.getDescription(),
                document.getCategory(),
                document.getStatus(),
                document.getCreatedBy(),
                document.getDueDate(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}

