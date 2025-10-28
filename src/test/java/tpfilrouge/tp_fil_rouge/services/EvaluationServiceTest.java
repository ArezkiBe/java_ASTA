package tpfilrouge.tp_fil_rouge.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.Evaluation;
import tpfilrouge.tp_fil_rouge.modele.repository.EvaluationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    private Evaluation evaluation;
    private Apprenti apprenti;

    @BeforeEach
    void setUp() {
        apprenti = new Apprenti();
        apprenti.setId(1);
        apprenti.setNom("Martin");
        apprenti.setPrenom("Paul");

        evaluation = new Evaluation();
        evaluation.setId(1);
        evaluation.setThemeSujet("Java Spring Boot");
        evaluation.setType("Soutenance");
        evaluation.setDateSoutenance("2024-06-15");
        evaluation.setNoteFinale(16.0);
        evaluation.setCommentaires("Bonne maîtrise");
        evaluation.setApprenti(apprenti);
    }

    @Test
    void testGetAllEvaluations() {
        // Given
        List<Evaluation> evaluations = Arrays.asList(evaluation);
        when(evaluationRepository.findAll()).thenReturn(evaluations);

        // When
        List<Evaluation> result = evaluationService.getAllEvaluations();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNoteFinale()).isEqualTo(16.0);
        verify(evaluationRepository, times(1)).findAll();
    }

    @Test
    void testGetEvaluationById_Success() {
        // Given
        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));

        // When
        Optional<Evaluation> result = evaluationService.getEvaluationById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getThemeSujet()).isEqualTo("Java Spring Boot");
        verify(evaluationRepository, times(1)).findById(1);
    }

    @Test
    void testCreateEvaluation() {
        // Given
        when(evaluationRepository.save(evaluation)).thenReturn(evaluation);

        // When
        Evaluation result = evaluationService.createEvaluation(evaluation);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNoteFinale()).isEqualTo(16.0);
        verify(evaluationRepository, times(1)).save(evaluation);
    }

    @Test
    void testUpdateEvaluation_Success() {
        // Given
        Evaluation evaluationMiseAJour = new Evaluation();
        evaluationMiseAJour.setThemeSujet("Angular Frontend");
        evaluationMiseAJour.setNoteFinale(14.0);
        evaluationMiseAJour.setCommentaires("À améliorer");

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(evaluation);

        // When
        Evaluation result = evaluationService.updateEvaluation(1, evaluationMiseAJour);

        // Then
        assertThat(result).isNotNull();
        verify(evaluationRepository, times(1)).findById(1);
        verify(evaluationRepository, times(1)).save(evaluation);
    }

    @Test
    void testUpdateEvaluation_NotFound() {
        // Given
        when(evaluationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> evaluationService.updateEvaluation(999, evaluation))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Évaluation non trouvée");
    }

    @Test
    void testDeleteEvaluation() {
        // Given
        doNothing().when(evaluationRepository).deleteById(1);

        // When
        evaluationService.deleteEvaluation(1);

        // Then
        verify(evaluationRepository, times(1)).deleteById(1);
    }
}