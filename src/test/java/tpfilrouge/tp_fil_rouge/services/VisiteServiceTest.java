package tpfilrouge.tp_fil_rouge.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;
import tpfilrouge.tp_fil_rouge.modele.repository.VisiteRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisiteServiceTest {

    @Mock
    private VisiteRepository visiteRepository;

    @InjectMocks
    private VisiteService visiteService;

    private Visite visite;
    private Apprenti apprenti;

    @BeforeEach
    void setUp() {
        apprenti = new Apprenti();
        apprenti.setId(1);
        apprenti.setNom("Dupont");
        apprenti.setPrenom("Jean");

        visite = new Visite();
        visite.setId(1);
        visite.setDate(LocalDate.now());
        visite.setStatut("Programmée");
        visite.setFormat("Présentiel");
        visite.setCommentaires("Test");
        visite.setApprenti(apprenti);
    }

    @Test
    void testGetAllVisites() {
        // Given
        List<Visite> visites = Arrays.asList(visite);
        when(visiteRepository.findAll()).thenReturn(visites);

        // When
        List<Visite> result = visiteService.getAllVisites();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo("Programmée");
        verify(visiteRepository, times(1)).findAll();
    }

    @Test
    void testGetVisiteById_Success() {
        // Given
        when(visiteRepository.findById(1)).thenReturn(Optional.of(visite));

        // When
        Optional<Visite> result = visiteService.getVisiteById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStatut()).isEqualTo("Programmée");
        verify(visiteRepository, times(1)).findById(1);
    }

    @Test
    void testCreateVisite() {
        // Given
        when(visiteRepository.save(visite)).thenReturn(visite);

        // When
        Visite result = visiteService.createVisite(visite);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatut()).isEqualTo("Programmée");
        verify(visiteRepository, times(1)).save(visite);
    }

    @Test
    void testUpdateVisite_Success() {
        // Given
        Visite visiteMisAJour = new Visite();
        visiteMisAJour.setDate(LocalDate.now().plusDays(1));
        visiteMisAJour.setStatut("Annulée");
        visiteMisAJour.setFormat("Distanciel");
        visiteMisAJour.setCommentaires("Visite annulée");

        when(visiteRepository.findById(1)).thenReturn(Optional.of(visite));
        when(visiteRepository.save(any(Visite.class))).thenReturn(visite);

        // When
        Visite result = visiteService.updateVisite(1, visiteMisAJour);

        // Then
        assertThat(result).isNotNull();
        verify(visiteRepository, times(1)).findById(1);
        verify(visiteRepository, times(1)).save(visite);
    }

    @Test
    void testUpdateVisite_NotFound() {
        // Given
        when(visiteRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> visiteService.updateVisite(999, visite))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Visite non trouvée");
    }

    @Test
    void testDeleteVisite() {
        // Given
        doNothing().when(visiteRepository).deleteById(1);

        // When
        visiteService.deleteVisite(1);

        // Then
        verify(visiteRepository, times(1)).deleteById(1);
    }
}