package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.MaitreApprentissage;
import tpfilrouge.tp_fil_rouge.modele.repository.MaitreApprentissageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MaitreApprentissageService {

    private final MaitreApprentissageRepository maitreApprentissageRepository;

    @Autowired
    public MaitreApprentissageService(MaitreApprentissageRepository maitreApprentissageRepository) {
        this.maitreApprentissageRepository = maitreApprentissageRepository;
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
            .orElseThrow(() -> new RuntimeException("Maître d'apprentissage non trouvé"));

        maitre.setNom(maitreDetails.getNom());
        maitre.setPrenom(maitreDetails.getPrenom());
        maitre.setEmail(maitreDetails.getEmail());
        maitre.setTelephone(maitreDetails.getTelephone());
        maitre.setPoste(maitreDetails.getPoste());
        maitre.setRemarques(maitreDetails.getRemarques());

        return maitreApprentissageRepository.save(maitre);
    }

    public void deleteMaitre(Integer id) {
        maitreApprentissageRepository.deleteById(id);
    }
}
