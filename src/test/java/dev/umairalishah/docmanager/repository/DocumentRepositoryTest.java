package dev.umairalishah.docmanager.repository;

import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.model.DocumentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DocumentRepositoryTest {

    @org.springframework.beans.factory.annotation.Autowired
    private DocumentRepository repository;

    @Test
    void findByStatusReturnsOnlyMatchingDocuments() {
        repository.save(new Document("CTS-2026-100001", "Draft memo", null,
                DocumentCategory.INTERNAL_MEMO, "umair.shah", null));

        Document approved = new Document("CTS-2026-100002", "Approved contract", null,
                DocumentCategory.CONTRACT, "umair.shah", null);
        approved.setStatus(DocumentStatus.APPROVED);
        repository.save(approved);

        var results = repository.findByStatus(DocumentStatus.APPROVED, PageRequest.of(0, 10));

        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().get(0).getReferenceNumber()).isEqualTo("CTS-2026-100002");
    }

    @Test
    void existsByReferenceNumberDetectsDuplicates() {
        repository.save(new Document("CTS-2026-100003", "Invoice", null,
                DocumentCategory.INVOICE, "umair.shah", null));

        assertThat(repository.existsByReferenceNumber("CTS-2026-100003")).isTrue();
        assertThat(repository.existsByReferenceNumber("CTS-2026-999999")).isFalse();
    }

    @Test
    void titleSearchIsCaseInsensitive() {
        repository.save(new Document("CTS-2026-100004", "Annual Compliance Report", null,
                DocumentCategory.REPORT, "umair.shah", null));

        var results = repository.findByTitleContainingIgnoreCase("compliance", PageRequest.of(0, 10));

        assertThat(results.getTotalElements()).isEqualTo(1);
    }
}

