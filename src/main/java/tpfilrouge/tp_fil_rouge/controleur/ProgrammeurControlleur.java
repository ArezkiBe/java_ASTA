package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.entite.Programmeur;

import java.util.List;
import java.util.Optional;

@Controller
//@RestController
@RequestMapping("/tpfilrouge")
public class ProgrammeurControlleur {

    //@Autowired
    private final ProgrammeurService programmeurService;

    public ProgrammeurControlleur(ProgrammeurService programmeurService) {
        this.programmeurService = programmeurService;
    }

    @GetMapping("/programmeurs")
    public String afficherProgrammeurs(Model model) {

        model.addAttribute("programmeurs", this.programmeurService.getProgrammeurs());
        return "listeProgrammeurs";
        //return programmeurService.getProgrammeurs();
    }

    @GetMapping("/unProgrammeur/{idProgrammeur}")
    public Optional<Programmeur> afficherUnProgrammeur(@PathVariable Integer idProgrammeur) {

        return programmeurService.getUnProgrammeur(idProgrammeur);
    }

    @DeleteMapping("/supprimerProgrammeur/{idProgrammeur}")
//    @GetMapping("/supprimerProgrammeur/{idProgrammeur}")    // A activer si l'on veut être conforme aux specs HTML % GET et POST
//    public void deleteProgrammeur(@PathVariable("idProgrammeur") Integer idProg) {    // Sans Thymeleaf
    public String deleteProgrammeur(@PathVariable("idProgrammeur") Integer idProg) {   // Avec Thymeleaf
        programmeurService.deleteUnProgrammeur(idProg);
        return "redirect:/tpfilrouge/programmeurs";  // Affichage du tableau avec la liste des programmeurs après la suppression
    }

    @PostMapping("/ajouterProgrammeur")
//    public void creerProgrammeur(@RequestBody Programmeur programmeur){
    public String creerProgrammeur(@ModelAttribute Programmeur programmeur){  // ATTENTION! Notez bien l'usage de ModelAttribute au lieu de RequestBody
        programmeurService.addUnProgrammeur(programmeur);
        return "redirect:/tpfilrouge/programmeurs";  // Affichage du tableau avec la liste des programmeurs après l'ajout
    }

    //Pour que Thymeleaf affiche le formulaire vierge
    // permettant d'ajouter un programmeur
    @GetMapping("/preparerAjoutProgrammeur")
    public String preparerAjoutPassager(Model model) {
        Programmeur newProgrammeur = new Programmeur();
        model.addAttribute("nouveauProgrammeur", newProgrammeur);
        return "nouveauProgrammeur";
    }

    // % Thymeleaf
    @GetMapping("/preparerModifProgrammeur/{idProgrammeur}")
    public String preparerModifProgrammeur(@PathVariable Integer idProgrammeur, Model model)  {
        Optional<Programmeur> programmeurToUpdate = programmeurService.getUnProgrammeur(idProgrammeur);
        model.addAttribute("programmeurToUpdate", programmeurToUpdate.orElseThrow());
        return "detailsProgrammeur";
    }

    @PutMapping("modifier/{idProgrammeur}")
//    public void modifierProgrammeur(@PathVariable Integer idProgrammeur,@RequestBody Programmeur programmeurModified ){
    public String modifierProgrammeur(@PathVariable Integer idProgrammeur,@ModelAttribute Programmeur programmeurModified ){ // ATTENTION! Notez bien l'usage de ModelAttribute au lieu de RequestBody
        programmeurService.updateUnProgrammeur(idProgrammeur,programmeurModified);
        return "redirect:/tpfilrouge/programmeurs";
    }
}
