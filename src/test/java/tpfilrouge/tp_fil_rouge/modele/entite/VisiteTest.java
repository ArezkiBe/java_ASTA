package tpfilrouge.tp_fil_rouge.modele.entite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class VisiteTest {

    private Visite visite;
    private Apprenti apprenti;

    @BeforeEach
    void setUp() {
        apprenti = new Apprenti();
        apprenti.setId(1);
        apprenti.setNom("Dupont");
        apprenti.setPrenom("Jean");

        visite = new Visite();
    }

    @Test
    void testVisiteCreation() {
        // When
        visite.setId(1);
        visite.setDate(LocalDate.now());
        visite.setStatut("Programmée");
        visite.setFormat("Présentiel");
        visite.setCommentaires("Test de visite");
        visite.setApprenti(apprenti);

        // Then
        assertThat(visite.getId()).isEqualTo(1);
        assertThat(visite.getStatut()).isEqualTo("Programmée");
        assertThat(visite.getFormat()).isEqualTo("Présentiel");
        assertThat(visite.getCommentaires()).isEqualTo("Test de visite");
        assertThat(visite.getApprenti()).isEqualTo(apprenti);
        assertThat(visite.getDate()).isNotNull();
    }

    @Test
    void testVisiteStatutModification() {
        // Given
        visite.setStatut("Programmée");

        // When
        visite.setStatut("Annulée");

        // Then
        assertThat(visite.getStatut()).isEqualTo("Annulée");
    }

    @Test
    void testVisiteEquals() {
        // Given
        Visite visite1 = new Visite();
        visite1.setId(1);

        Visite visite2 = new Visite();
        visite2.setId(1);

        Visite visite3 = new Visite();
        visite3.setId(2);

        // Then
        assertThat(visite1).isEqualTo(visite2);
        assertThat(visite1).isNotEqualTo(visite3);
        assertThat(visite1).isNotEqualTo(null);
    }

    @Test
    void testVisiteHashCode() {
        // Given
        Visite visite1 = new Visite();
        visite1.setId(1);

        Visite visite2 = new Visite();
        visite2.setId(1);

        // Then
        assertThat(visite1.hashCode()).isEqualTo(visite2.hashCode());
    }
}