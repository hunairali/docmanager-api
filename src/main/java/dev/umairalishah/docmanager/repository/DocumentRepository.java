package dev.umairalishah.docmanager.repository;

import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.model.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);

    Page<Document> findByCategory(DocumentCategory category, Pageable pageable);

    Page<Document> findByTitleContainingIgnoreCase(String titleFragment, Pageable pageable);

    long countByStatus(DocumentStatus status);

    boolean existsByReferenceNumber(String referenceNumber);
}

