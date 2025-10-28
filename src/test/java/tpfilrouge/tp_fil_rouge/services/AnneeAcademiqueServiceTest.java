package tpfilrouge.tp_fil_rouge.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnneeAcademiqueServiceTest {

    @Mock
    private AnneeAcademiqueRepository anneeAcademiqueRepository;

    @InjectMocks
    private AnneeAcademiqueService anneeAcademiqueService;

    private AnneeAcademique annee;

    @BeforeEach
    void setUp() {
        annee = new AnneeAcademique();
        annee.setId(1);
        annee.setAnnee("2024-2025");
        annee.setEstCourante(true);
    }

    @Test
    void getAllAnnees_ShouldReturnList() {
        // Given
        List<AnneeAcademique> annees = Arrays.asList(annee);
        when(anneeAcademiqueRepository.findAll()).thenReturn(annees);

        // When
        List<AnneeAcademique> result = anneeAcademiqueService.getAllAnnees();

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void getAnneeCourante_ShouldReturnCurrentYear() {
        // Given
        when(anneeAcademiqueRepository.findByEstCouranteTrue()).thenReturn(Optional.of(annee));

        // When
        Optional<AnneeAcademique> result = anneeAcademiqueService.getAnneeCourante();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEstCourante()).isTrue();
    }

    @Test
    void creerNouvelleAnnee_ShouldCreateYear() {
        // Given
        when(anneeAcademiqueRepository.existsByAnnee("2025-2026")).thenReturn(false);
        when(anneeAcademiqueRepository.save(any(AnneeAcademique.class))).thenReturn(annee);

        // When
        AnneeAcademique result = anneeAcademiqueService.creerNouvelleAnnee("2025-2026");

        // Then
        assertThat(result).isNotNull();
        verify(anneeAcademiqueRepository).save(any(AnneeAcademique.class));
    }

    @Test
    void creerNouvelleAnnee_WhenExists_ShouldThrowException() {
        // Given
        when(anneeAcademiqueRepository.existsByAnnee("2024-2025")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> anneeAcademiqueService.creerNouvelleAnnee("2024-2025"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("existe déjà");
    }
}