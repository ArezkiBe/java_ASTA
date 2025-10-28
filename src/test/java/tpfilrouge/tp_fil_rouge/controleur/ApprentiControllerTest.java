package tpfilrouge.tp_fil_rouge.controleur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApprentiController.class)
class ApprentiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApprentiService apprentiService;

    @Test
    void getAllApprentis_ShouldReturnList() throws Exception {
        // Given
        Apprenti apprenti = new Apprenti();
        apprenti.setId(1);
        apprenti.setNom("Test");
        List<Apprenti> apprentis = Arrays.asList(apprenti);
        when(apprentiService.getAllApprentis()).thenReturn(apprentis);

        // When & Then
        mockMvc.perform(get("/api/apprentis"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getApprentiById_ShouldReturnApprenti() throws Exception {
        // Given
        Apprenti apprenti = new Apprenti();
        apprenti.setId(1);
        apprenti.setNom("Test");
        when(apprentiService.getApprentiById(1)).thenReturn(apprenti);

        // When & Then
        mockMvc.perform(get("/api/apprentis/1"))
            .andExpect(status().isOk());
    }

    @Test
    void rechercherParNom_ShouldReturnResults() throws Exception {
        // Given
        Apprenti apprenti = new Apprenti();
        apprenti.setNom("Dupont");
        List<Apprenti> apprentis = Arrays.asList(apprenti);
        when(apprentiService.rechercherParNom("Dupont")).thenReturn(apprentis);

        // When & Then
        mockMvc.perform(get("/api/apprentis/recherche/nom")
                .param("q", "Dupont"))
            .andExpect(status().isOk());
    }
}