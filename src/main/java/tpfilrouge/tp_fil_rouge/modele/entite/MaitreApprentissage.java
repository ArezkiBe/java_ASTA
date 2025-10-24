package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "maitre_apprentissage", schema = "base_asta_altn72")
public class MaitreApprentissage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "poste", length = 100)
    private String poste;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telephone", length = 15)
    private String telephone;

    @Column(name = "remarques", length = 500)
    private String remarques;
}

