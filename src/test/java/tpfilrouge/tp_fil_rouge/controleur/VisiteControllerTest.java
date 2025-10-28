package tpfilrouge.tp_fil_rouge.controleur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;
import tpfilrouge.tp_fil_rouge.services.VisiteService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisiteControllerTest {

    @Mock
    private VisiteService visiteService;

    @InjectMocks
    private VisiteController visiteController;

    private Visite visite;

    @BeforeEach
    void setUp() {
        visite = new Visite();
        visite.setId(1);
        visite.setDate(LocalDate.now());
        visite.setStatut("Programmée");
        visite.setFormat("Présentiel");
    }

    @Test
    void testGetAllVisites() {
        // Given
        List<Visite> visites = Arrays.asList(visite);
        when(visiteService.getAllVisites()).thenReturn(visites);

        // When
        ResponseEntity<List<Visite>> response = visiteController.getAllVisites();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getStatut()).isEqualTo("Programmée");
        verify(visiteService, times(1)).getAllVisites();
    }

    @Test
    void testGetVisiteById_Success() {
        // Given
        when(visiteService.getVisiteById(1)).thenReturn(Optional.of(visite));

        // When
        ResponseEntity<Visite> response = visiteController.getVisiteById(1);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatut()).isEqualTo("Programmée");
        verify(visiteService, times(1)).getVisiteById(1);
    }

    @Test
    void testGetVisiteById_NotFound() {
        // Given
        when(visiteService.getVisiteById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Visite> response = visiteController.getVisiteById(999);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        verify(visiteService, times(1)).getVisiteById(999);
    }

    @Test
    void testCreateVisite() {
        // Given
        when(visiteService.createVisite(visite)).thenReturn(visite);

        // When
        ResponseEntity<Visite> response = visiteController.createVisite(visite);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatut()).isEqualTo("Programmée");
        verify(visiteService, times(1)).createVisite(visite);
    }
}