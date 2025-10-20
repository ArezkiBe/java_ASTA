package tpfilrouge.tp_fil_rouge.modele.entite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "programmeur", schema = "base_altn72")
public class Programmeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "langage", length = 100)
    private String langage;

    @Column(name = "livre", length = 150)
    private String livre;

    @Column(name = "salaire")
    private Double salaire;

}