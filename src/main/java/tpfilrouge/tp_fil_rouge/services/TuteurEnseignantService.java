package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.modele.repository.TuteurEnseignantRepository;

import java.util.Optional;

@Service
public class TuteurEnseignantService {

    private final TuteurEnseignantRepository tuteurEnseignantRepository;

    @Autowired
    public TuteurEnseignantService(TuteurEnseignantRepository tuteurEnseignantRepository) {
        this.tuteurEnseignantRepository = tuteurEnseignantRepository;
    }

    public Optional<TuteurEnseignant> getTuteurByLogin(String login) {
        return tuteurEnseignantRepository.findByLogin(login);
    }
}
