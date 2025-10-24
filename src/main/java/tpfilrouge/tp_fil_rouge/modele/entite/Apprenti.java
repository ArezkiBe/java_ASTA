package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "apprenti", schema = "base_asta_altn72")
public class Apprenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "telephone", length = 15)
    private String telephone;

    @Column(name = "programme", length = 50)
    private String programme = "M2-PRO";

    @Column(name = "majeure", length = 100)
    private String majeure = "Digital Transformation";

    @Column(name = "est_archive", nullable = false)
    private Boolean estArchive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuteur_enseignant_id", nullable = false)
    private TuteurEnseignant tuteurEnseignant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maitre_apprentissage_id")
    private MaitreApprentissage maitreApprentissage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenti_id")
    private java.util.List<Visite> visites;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenti_id")
    private java.util.List<Evaluation> evaluations;

    @Column(name = "feedback_tuteur_enseignant", length = 500)
    private String feedbackTuteurEnseignant;

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Apprenti apprenti = (Apprenti) obj;
        return id != null && id.equals(apprenti.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Apprenti{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", estArchive=" + estArchive +
                '}';
    }
}