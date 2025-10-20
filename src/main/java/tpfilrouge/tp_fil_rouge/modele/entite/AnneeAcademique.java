package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "annee_academique", schema = "base_asta_altn72")
public class AnneeAcademique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "annee", nullable = false, unique = true, length = 20)
    private String annee;

    @Column(name = "est_courante", nullable = false)
    private Boolean estCourante = false;

    @OneToMany(mappedBy = "anneeAcademique", fetch = FetchType.LAZY)
    private List<Apprenti> apprentis;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AnneeAcademique that = (AnneeAcademique) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AnneeAcademique{" +
                "id=" + id +
                ", annee='" + annee + '\'' +
                ", estCourante=" + estCourante +
                '}';
    }
}