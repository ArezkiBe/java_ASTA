package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.Evaluation;
import tpfilrouge.tp_fil_rouge.modele.repository.EvaluationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<Evaluation> getEvaluationById(Integer id) {
        return evaluationRepository.findById(id);
    }

    public Evaluation createEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    public Evaluation updateEvaluation(Integer id, Evaluation evaluationDetails) {
        Evaluation evaluation = evaluationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        evaluation.setType(evaluationDetails.getType());
        evaluation.setThemeSujet(evaluationDetails.getThemeSujet());
        evaluation.setNoteFinale(evaluationDetails.getNoteFinale());
        evaluation.setDateSoutenance(evaluationDetails.getDateSoutenance());
        evaluation.setCommentaires(evaluationDetails.getCommentaires());

        return evaluationRepository.save(evaluation);
    }

    public void deleteEvaluation(Integer id) {
        // Vérifier si l'évaluation existe
        if (!evaluationRepository.existsById(id)) {
            throw new RuntimeException("Évaluation non trouvée avec l'ID : " + id);
        }
        
        evaluationRepository.deleteById(id);
    }
}

