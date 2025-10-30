package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.exceptions.MaitreApprentissageNonTrouveException;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.MaitreApprentissage;
import tpfilrouge.tp_fil_rouge.modele.repository.ApprentiRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.MaitreApprentissageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MaitreApprentissageService {

    private final MaitreApprentissageRepository maitreApprentissageRepository;
    private final ApprentiRepository apprentiRepository;

    @Autowired
    public MaitreApprentissageService(MaitreApprentissageRepository maitreApprentissageRepository,
                                    ApprentiRepository apprentiRepository) {
        this.maitreApprentissageRepository = maitreApprentissageRepository;
        this.apprentiRepository = apprentiRepository;
    }

    public List<MaitreApprentissage> getAllMaitres() {
        return maitreApprentissageRepository.findAll();
    }

    public Optional<MaitreApprentissage> getMaitreById(Integer id) {
        return maitreApprentissageRepository.findById(id);
    }

    public MaitreApprentissage createMaitre(MaitreApprentissage maitre) {
        return maitreApprentissageRepository.save(maitre);
    }

    public MaitreApprentissage updateMaitre(Integer id, MaitreApprentissage maitreDetails) {
        MaitreApprentissage maitre = maitreApprentissageRepository.findById(id)
            .orElseThrow(() -> new MaitreApprentissageNonTrouveException(id));

        maitre.setNom(maitreDetails.getNom());
        maitre.setPrenom(maitreDetails.getPrenom());
        maitre.setEmail(maitreDetails.getEmail());
        maitre.setTelephone(maitreDetails.getTelephone());
        maitre.setPoste(maitreDetails.getPoste());
        maitre.setRemarques(maitreDetails.getRemarques());

        return maitreApprentissageRepository.save(maitre);
    }

    public void deleteMaitre(Integer id) {
        // Vérifier si le maître d'apprentissage existe
        if (!maitreApprentissageRepository.existsById(id)) {
            throw new RuntimeException("Maître d'apprentissage non trouvé avec l'ID : " + id);
        }
        
        // Vérifier s'il y a des apprentis associés
        List<Apprenti> apprentisAssocies = apprentiRepository.findAll().stream()
                .filter(apprenti -> apprenti.getMaitreApprentissage() != null && 
                        apprenti.getMaitreApprentissage().getId().equals(id))
                .toList();
                
        if (!apprentisAssocies.isEmpty()) {
            throw new RuntimeException("Impossible de supprimer ce maître d'apprentissage : il encadre " + 
                apprentisAssocies.size() + " apprenti(s). " +
                "Veuillez d'abord réassigner les apprentis à un autre maître ou supprimer les apprentis.");
        }
        
        maitreApprentissageRepository.deleteById(id);
    }
}
