package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "visite", schema = "base_asta_altn72")
public class Visite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "format", length = 50)
    private String format;

    @Column(name = "commentaires", length = 500)
    private String commentaires;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenti_id")
    private Apprenti apprenti;

    @Column(name = "commentaire_tuteur", length = 500)
    private String commentaireTuteur;

    @Column(name = "statut", length = 50)
    private String statut; // Programmée, Réalisée, Annulée

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Visite visite = (Visite) obj;
        return id != null && id.equals(visite.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Visite{" +
                "id=" + id +
                ", date=" + date +
                ", format='" + format + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}

