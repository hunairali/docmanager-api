package dev.umairalishah.docmanager.service;

import dev.umairalishah.docmanager.dto.DocumentRequest;
import dev.umairalishah.docmanager.dto.StatusChangeRequest;
import dev.umairalishah.docmanager.exception.DocumentNotFoundException;
import dev.umairalishah.docmanager.exception.InvalidStatusTransitionException;
import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.model.DocumentStatus;
import dev.umairalishah.docmanager.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    private DocumentService service;

    @BeforeEach
    void setUp() {
        service = new DocumentService(repository, new ReferenceNumberGenerator());
    }

    @Test
    void createSavesDocumentInDraftStatus() {
        DocumentRequest request = new DocumentRequest(
                "Quarterly compliance report", "Q3 report for review",
                DocumentCategory.REPORT, "umair.shah", LocalDate.now().plusDays(7));

        when(repository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Document result = service.create(request);

        assertThat(result.getStatus()).isEqualTo(DocumentStatus.DRAFT);
        assertThat(result.getTitle()).isEqualTo("Quarterly compliance report");
        assertThat(result.getReferenceNumber()).startsWith("CTS-");
        verify(repository).save(any(Document.class));
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void changeStatusAllowsDraftToPendingReview() {
        Document document = new Document("CTS-2026-000001", "Vendor contract renewal", null,
                DocumentCategory.CONTRACT, "umair.shah", null);
        when(repository.findById(1L)).thenReturn(Optional.of(document));

        Document updated = service.changeStatus(1L, new StatusChangeRequest(DocumentStatus.PENDING_REVIEW));

        assertThat(updated.getStatus()).isEqualTo(DocumentStatus.PENDING_REVIEW);
    }

    @Test
    void changeStatusRejectsInvalidTransition() {
        Document document = new Document("CTS-2026-000002", "Vendor contract renewal", null,
                DocumentCategory.CONTRACT, "umair.shah", null);
        when(repository.findById(2L)).thenReturn(Optional.of(document));

        assertThatThrownBy(() -> service.changeStatus(2L, new StatusChangeRequest(DocumentStatus.ARCHIVED)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void deleteThrowsWhenDocumentDoesNotExist() {
        when(repository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(5L))
                .isInstanceOf(DocumentNotFoundException.class);
    }
}

