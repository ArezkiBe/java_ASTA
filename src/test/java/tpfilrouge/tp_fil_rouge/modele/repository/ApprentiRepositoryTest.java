package tpfilrouge.tp_fil_rouge.modele.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ApprentiRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApprentiRepository apprentiRepository;

    @Test
    void findByEstArchiveFalse_ShouldReturnActiveApprentis() {
        // Given
        Apprenti apprenti = new Apprenti();
        apprenti.setNom("Test");
        apprenti.setPrenom("Jean");
        apprenti.setEmail("test@email.com");
        apprenti.setEstArchive(false);
        entityManager.persistAndFlush(apprenti);

        // When
        List<Apprenti> result = apprentiRepository.findByEstArchiveFalseOrderByNomAscPrenomAsc();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Test");
    }

    @Test
    void existsByEmail_ShouldReturnTrue() {
        // Given
        Apprenti apprenti = new Apprenti();
        apprenti.setNom("Test");
        apprenti.setPrenom("Jean");
        apprenti.setEmail("unique@email.com");
        apprenti.setEstArchive(false);
        entityManager.persistAndFlush(apprenti);

        // When
        boolean exists = apprentiRepository.existsByEmail("unique@email.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse() {
        // When
        boolean exists = apprentiRepository.existsByEmail("notfound@email.com");

        // Then
        assertThat(exists).isFalse();
    }
}