package dev.umairalishah.docmanager.service;

import dev.umairalishah.docmanager.dto.DocumentRequest;
import dev.umairalishah.docmanager.dto.StatusChangeRequest;
import dev.umairalishah.docmanager.exception.DocumentNotFoundException;
import dev.umairalishah.docmanager.exception.InvalidStatusTransitionException;
import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.model.DocumentStatus;
import dev.umairalishah.docmanager.repository.DocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository repository;
    private final ReferenceNumberGenerator referenceNumberGenerator;

    public DocumentService(DocumentRepository repository, ReferenceNumberGenerator referenceNumberGenerator) {
        this.repository = repository;
        this.referenceNumberGenerator = referenceNumberGenerator;
    }

    public Document create(DocumentRequest request) {
        String referenceNumber = referenceNumberGenerator.next();
        Document document = new Document(
                referenceNumber,
                request.title(),
                request.description(),
                request.category(),
                request.createdBy(),
                request.dueDate()
        );
        return repository.save(document);
    }

    @Transactional(readOnly = true)
    public Document getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Document> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Document> findByStatus(DocumentStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Document> findByCategory(DocumentCategory category, Pageable pageable) {
        return repository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Document> search(String titleFragment, Pageable pageable) {
        return repository.findByTitleContainingIgnoreCase(titleFragment, pageable);
    }

    public Document update(Long id, DocumentRequest request) {
        Document document = getById(id);
        document.setTitle(request.title());
        document.setDescription(request.description());
        document.setCategory(request.category());
        document.setDueDate(request.dueDate());
        return document;
    }

    public Document changeStatus(Long id, StatusChangeRequest request) {
        Document document = getById(id);
        DocumentStatus current = document.getStatus();
        DocumentStatus target = request.status();
        if (!current.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(current, target);
        }
        document.setStatus(target);
        return document;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }
        repository.deleteById(id);
    }
}

