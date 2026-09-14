package dev.umairalishah.docmanager.controller;

import dev.umairalishah.docmanager.dto.DocumentRequest;
import dev.umairalishah.docmanager.dto.DocumentResponse;
import dev.umairalishah.docmanager.dto.PageResponse;
import dev.umairalishah.docmanager.dto.StatusChangeRequest;
import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.model.DocumentStatus;
import dev.umairalishah.docmanager.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Document / correspondence lifecycle management")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Register a new document")
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest request) {
        Document created = service.create(request);
        return ResponseEntity.created(URI.create("/api/documents/" + created.getId()))
                .body(DocumentResponse.from(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a document by id")
    public DocumentResponse getById(@PathVariable Long id) {
        return DocumentResponse.from(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "List documents, optionally filtered by status, category, or title search")
    public PageResponse<DocumentResponse> list(
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) DocumentCategory category,
            @RequestParam(required = false) String titleContains,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        if (status != null) {
            return PageResponse.from(service.findByStatus(status, pageable).map(DocumentResponse::from));
        }
        if (category != null) {
            return PageResponse.from(service.findByCategory(category, pageable).map(DocumentResponse::from));
        }
        if (titleContains != null && !titleContains.isBlank()) {
            return PageResponse.from(service.search(titleContains, pageable).map(DocumentResponse::from));
        }
        return PageResponse.from(service.list(pageable).map(DocumentResponse::from));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a document's editable fields")
    public DocumentResponse update(@PathVariable Long id, @Valid @RequestBody DocumentRequest request) {
        return DocumentResponse.from(service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transition a document's workflow status")
    public DocumentResponse changeStatus(@PathVariable Long id, @Valid @RequestBody StatusChangeRequest request) {
        return DocumentResponse.from(service.changeStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a document")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

