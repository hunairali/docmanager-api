package dev.umairalishah.docmanager.dto;

import dev.umairalishah.docmanager.model.DocumentCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Payload for creating or fully updating a document.
 */
public record DocumentRequest(

        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @Size(max = 2000, message = "description must be at most 2000 characters")
        String description,

        @NotNull(message = "category is required")
        DocumentCategory category,

        @NotBlank(message = "createdBy is required")
        @Size(max = 120)
        String createdBy,

        @FutureOrPresent(message = "dueDate cannot be in the past")
        LocalDate dueDate
) {
}

