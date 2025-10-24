package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.modele.repository.TuteurEnseignantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TuteurEnseignantService {

    private final TuteurEnseignantRepository tuteurEnseignantRepository;

    @Autowired
    public TuteurEnseignantService(TuteurEnseignantRepository tuteurEnseignantRepository) {
        this.tuteurEnseignantRepository = tuteurEnseignantRepository;
    }

    public List<TuteurEnseignant> getAllTuteurs() {
        return tuteurEnseignantRepository.findAll();
    }

    public Optional<TuteurEnseignant> getTuteurById(Integer id) {
        return tuteurEnseignantRepository.findById(id);
    }

    public TuteurEnseignant createTuteur(TuteurEnseignant tuteur) {
        return tuteurEnseignantRepository.save(tuteur);
    }

    public TuteurEnseignant updateTuteur(Integer id, TuteurEnseignant tuteurDetails) {
        TuteurEnseignant tuteur = tuteurEnseignantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tuteur enseignant non trouvé"));

        tuteur.setLogin(tuteurDetails.getLogin());
        tuteur.setMotDePasse(tuteurDetails.getMotDePasse());
        tuteur.setNom(tuteurDetails.getNom());
        tuteur.setPrenom(tuteurDetails.getPrenom());

        return tuteurEnseignantRepository.save(tuteur);
    }

    public void deleteTuteur(Integer id) {
        tuteurEnseignantRepository.deleteById(id);
    }
}
