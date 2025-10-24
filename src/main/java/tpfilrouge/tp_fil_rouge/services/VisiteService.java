package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;
import tpfilrouge.tp_fil_rouge.modele.repository.VisiteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VisiteService {

    private final VisiteRepository visiteRepository;

    @Autowired
    public VisiteService(VisiteRepository visiteRepository) {
        this.visiteRepository = visiteRepository;
    }

    public List<Visite> getAllVisites() {
        return visiteRepository.findAll();
    }

    public Optional<Visite> getVisiteById(Integer id) {
        return visiteRepository.findById(id);
    }

    public Visite createVisite(Visite visite) {
        return visiteRepository.save(visite);
    }

    public Visite updateVisite(Integer id, Visite visiteDetails) {
        Visite visite = visiteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Visite non trouvée"));

        visite.setDate(visiteDetails.getDate());
        visite.setFormat(visiteDetails.getFormat());
        visite.setCommentaires(visiteDetails.getCommentaires());

        return visiteRepository.save(visite);
    }

    public void deleteVisite(Integer id) {
        visiteRepository.deleteById(id);
    }
}
