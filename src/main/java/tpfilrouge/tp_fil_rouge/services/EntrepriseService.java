package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.Entreprise;
import tpfilrouge.tp_fil_rouge.modele.repository.EntrepriseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    public EntrepriseService(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Optional<Entreprise> getEntrepriseById(Integer id) {
        return entrepriseRepository.findById(id);
    }

    public Entreprise createEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    public Entreprise updateEntreprise(Integer id, Entreprise entrepriseDetails) {
        Entreprise entreprise = entrepriseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        entreprise.setRaisonSociale(entrepriseDetails.getRaisonSociale());
        entreprise.setAdresse(entrepriseDetails.getAdresse());
        entreprise.setInformationsUtilesAccesLocaux(entrepriseDetails.getInformationsUtilesAccesLocaux());

        return entrepriseRepository.save(entreprise);
    }

    public void deleteEntreprise(Integer id) {
        // Vérifier si l'entreprise existe
        if (!entrepriseRepository.existsById(id)) {
            throw new RuntimeException("Entreprise non trouvée avec l'ID : " + id);
        }
        
        // Cette méthode nécessiterait l'ajout d'une vérification pour les apprentis et maîtres associés
        // Pour l'instant, on maintient la suppression simple mais avec vérification d'existence
        entrepriseRepository.deleteById(id);
    }
}

