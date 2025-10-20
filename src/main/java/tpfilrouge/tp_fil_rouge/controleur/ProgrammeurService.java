package tpfilrouge.tp_fil_rouge.controleur;


import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.exceptions.ProgrammeurNonTrouveException;
import tpfilrouge.tp_fil_rouge.modele.entite.Programmeur;
import tpfilrouge.tp_fil_rouge.modele.repository.ProgrammeurRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProgrammeurService {

//    @Autowired
    private final ProgrammeurRepository programmeurRepository;

    public ProgrammeurService(ProgrammeurRepository programmeurRepository) {
        this.programmeurRepository = programmeurRepository;
    }


    public List<Programmeur> getProgrammeurs() {
        return programmeurRepository.findAll();
    }


    public Optional<Programmeur> getUnProgrammeur(Integer idProgrammeur) {
        Optional<Programmeur> unProgrammeur = programmeurRepository.findById(idProgrammeur);

        return Optional.ofNullable(
                unProgrammeur.orElseThrow(
                        () -> new ProgrammeurNonTrouveException(
                                "Le programmeur dont l\'id est " + idProgrammeur + " n\'existe pas")));
    }

    @Transactional
    public void deleteUnProgrammeur(Integer idProgrammeur) {


        Optional<Programmeur> unProgrammeur = programmeurRepository.findById(idProgrammeur);

        if (unProgrammeur.isPresent()) {
            programmeurRepository.deleteById(idProgrammeur);
        } else {
            throw new ProgrammeurNonTrouveException("Le programmeur dont l\'id est " + idProgrammeur + " n'existe pas");
        }
    }

    @Transactional
    public String addUnProgrammeur(Programmeur programmeur) {

        try {
            programmeurRepository.save(programmeur);
            return "Le programmeur a bien été ajouté";
        } catch (Exception e) {
            throw new IllegalStateException("L'ajout du programmeur a échoué");
        }
    }

    @Transactional
    public String updateUnProgrammeur(Integer idProgrammeur, Programmeur programmeurModified) {

        Optional<Programmeur> programmeurToModify = programmeurRepository.findById(idProgrammeur);

        if (programmeurToModify.isPresent()) {
            try{
                BeanUtils.copyProperties(programmeurToModify, programmeurModified, "id");
                //programmeurModified.setId(idProgrammeur);
                programmeurRepository.save(programmeurToModify.orElseThrow());
                return "Le programmeur dont l\'id est " + idProgrammeur + " a été mis à jour";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else  {
            throw new IllegalStateException("Le programmeur dont l\'id est " + idProgrammeur + " n'existe pas");
        }
    }
}