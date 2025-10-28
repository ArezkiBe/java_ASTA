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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenti_id")
    private Apprenti apprenti;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evaluation evaluation = (Evaluation) obj;
        return id != null && id.equals(evaluation.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", themeSujet='" + themeSujet + '\'' +
                ", noteFinale=" + noteFinale +
                '}';
    }
}

