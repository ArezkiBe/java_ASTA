package tpfilrouge.tp_fil_rouge.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.repository.ApprentiRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprentiServiceTest {

    @Mock
    private ApprentiRepository apprentiRepository;
    
    @Mock
    private AnneeAcademiqueRepository anneeAcademiqueRepository;

    @InjectMocks
    private ApprentiService apprentiService;

    private Apprenti apprenti;

    @BeforeEach
    void setUp() {
        apprenti = new Apprenti();
        apprenti.setId(1);
        apprenti.setNom("Test");
        apprenti.setPrenom("Jean");
    }

    @Test
    void createApprenti_ShouldSaveApprenti() {
        // Given
        when(apprentiRepository.save(apprenti)).thenReturn(apprenti);

        // When
        Apprenti result = apprentiService.createApprenti(apprenti);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Test");
    }

    @Test
    void getApprentiById_ShouldReturnApprenti() {
        // Given
        when(apprentiRepository.findById(1)).thenReturn(Optional.of(apprenti));

        // When
        Apprenti result = apprentiService.getApprentiById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getAllApprentis_ShouldReturnList() {
        // Given
        List<Apprenti> apprentis = Arrays.asList(apprenti);
        when(apprentiRepository.findAll()).thenReturn(apprentis);

        // When
        List<Apprenti> result = apprentiService.getAllApprentis();

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void rechercherParNom_ShouldReturnResults() {
        // Given
        List<Apprenti> apprentis = Arrays.asList(apprenti);
        when(apprentiRepository.findByNomContainingIgnoreCaseAndEstArchiveFalse("Test"))
            .thenReturn(apprentis);

        // When
        List<Apprenti> result = apprentiService.rechercherParNom("Test");

        // Then
        assertThat(result).hasSize(1);
    }
}