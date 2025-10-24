package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "evaluation", schema = "base_asta_altn72")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type", length = 50)
    private String type; // Memoire/Rapport ou Soutenance

    @Column(name = "theme_sujet", length = 200)
    private String themeSujet;

    @Column(name = "note_finale")
    private Double noteFinale;

    @Column(name = "date_soutenance")
    private String dateSoutenance;

    @Column(name = "commentaires", length = 500)
    private String commentaires;
}

