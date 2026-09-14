package dev.umairalishah.docmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.umairalishah.docmanager.dto.DocumentRequest;
import dev.umairalishah.docmanager.exception.DocumentNotFoundException;
import dev.umairalishah.docmanager.model.Document;
import dev.umairalishah.docmanager.model.DocumentCategory;
import dev.umairalishah.docmanager.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentService service;

    @Test
    void createReturns201AndLocationHeader() throws Exception {
        Document saved = new Document("CTS-2026-000010", "Renewal notice", "desc",
                DocumentCategory.CONTRACT, "umair.shah", LocalDate.now().plusDays(3));
        setId(saved, 10L);
        when(service.create(any())).thenReturn(saved);

        DocumentRequest request = new DocumentRequest("Renewal notice", "desc",
                DocumentCategory.CONTRACT, "umair.shah", LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.referenceNumber").value("CTS-2026-000010"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void createRejectsBlankTitle() throws Exception {
        DocumentRequest invalid = new DocumentRequest("", null, DocumentCategory.OTHER, "umair.shah", null);

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        when(service.getById(404L)).thenThrow(new DocumentNotFoundException(404L));

        mockMvc.perform(get("/api/documents/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listReturnsPagedResults() throws Exception {
        Document doc = new Document("CTS-2026-000011", "Board memo", null,
                DocumentCategory.INTERNAL_MEMO, "umair.shah", null);
        setId(doc, 11L);
        Page<Document> page = new PageImpl<>(List.of(doc));
        when(service.list(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].referenceNumber").value("CTS-2026-000011"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/api/documents/{id}", 11L))
                .andExpect(status().isNoContent());
    }

    private static void setId(Document document, Long id) {
        try {
            var field = Document.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(document, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}

