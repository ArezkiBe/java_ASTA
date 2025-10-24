package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mission", schema = "base_asta_altn72")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "mots_cles", length = 200)
    private String motsCles;

    @Column(name = "metier_cible", length = 100)
    private String metierCible;

    @Column(name = "commentaires", length = 500)
    private String commentaires;
}

