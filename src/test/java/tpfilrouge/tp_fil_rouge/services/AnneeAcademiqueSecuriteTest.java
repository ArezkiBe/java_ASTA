package tpfilrouge.tp_fil_rouge.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de sécurité pour le système d'année académique
 * Vérifie que les règles de progression séquentielle sont respectées
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class AnneeAcademiqueSecuriteTest {

    @Autowired
    private AnneeAcademiqueService anneeAcademiqueService;

    @Autowired
    private AnneeAcademiqueRepository anneeAcademiqueRepository;

    @BeforeEach
    void setUp() {
        // Nettoyer et initialiser les données de test
        anneeAcademiqueRepository.deleteAll();
        
        // Créer une année courante pour les tests
        AnneeAcademique annee2024 = new AnneeAcademique();
        annee2024.setAnnee("2024-2025");
        annee2024.setEstCourante(true);
        anneeAcademiqueRepository.save(annee2024);
    }

    @Test
    void testTransitionValide_AnneeSuivante() {
        // GIVEN: Année courante 2024-2025
        assertTrue(anneeAcademiqueService.getAnneeCourante().isPresent());
        
        // WHEN: Passer à l'année suivante normale
        AnneeAcademique nouvelleAnnee = anneeAcademiqueService.passerAAnneeSuivante("2025-2026");
        
        // THEN: Transition réussie
        assertNotNull(nouvelleAnnee);
        assertEquals("2025-2026", nouvelleAnnee.getAnnee());
        assertTrue(nouvelleAnnee.getEstCourante());
        
        // Vérifier que l'ancienne année n'est plus courante
        AnneeAcademique ancienne = anneeAcademiqueRepository.findByAnnee("2024-2025").orElse(null);
        assertNotNull(ancienne);
        assertFalse(ancienne.getEstCourante());
    }

    @Test
    void testTransitionInvalide_RetourArriere() {
        // GIVEN: Année courante 2024-2025
        
        // WHEN/THEN: Tentative de retour à une année antérieure
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            anneeAcademiqueService.passerAAnneeSuivante("2023-2024");
        });
        
        assertTrue(exception.getMessage().contains("Transition non autorisée"));
        assertTrue(exception.getMessage().contains("2024-2025"));
        assertTrue(exception.getMessage().contains("2023-2024"));
    }

    @Test
    void testTransitionInvalide_SautAnnee() {
        // GIVEN: Année courante 2024-2025
        
        // WHEN/THEN: Tentative de saut d'année
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            anneeAcademiqueService.passerAAnneeSuivante("2026-2027"); // Saute 2025-2026
        });
        
        assertTrue(exception.getMessage().contains("Transition non autorisée"));
    }

    @Test
    void testDefinirAnneeCouranteDesactive() {
        // WHEN/THEN: Tentative d'utiliser l'ancienne méthode non sécurisée
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            anneeAcademiqueService.definirAnneeCourante(1);
        });
        
        assertTrue(exception.getMessage().contains("définition manuelle de l'année courante n'est plus autorisée"));
    }

    @Test
    void testDefinirNouvelleAnneeCouranteDesactive() {
        // WHEN/THEN: Tentative d'utiliser l'ancienne méthode non sécurisée
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            anneeAcademiqueService.definirNouvelleAnneeCourante(1);
        });
        
        assertTrue(exception.getMessage().contains("définition manuelle de l'année courante n'est plus autorisée"));
    }

    @Test
    void testFormatAnneeInvalide() {
        // WHEN/THEN: Format d'année invalide
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            anneeAcademiqueService.passerAAnneeSuivante("2025"); // Format incorrect
        });
        
        assertTrue(exception.getMessage().contains("Transition non autorisée"));
    }

    @Test
    void testTransitionsMultiples() {
        // GIVEN: Plusieurs transitions séquentielles
        
        // WHEN: Transition 1: 2024-2025 → 2025-2026
        AnneeAcademique annee1 = anneeAcademiqueService.passerAAnneeSuivante("2025-2026");
        assertEquals("2025-2026", annee1.getAnnee());
        assertTrue(annee1.getEstCourante());
        
        // WHEN: Transition 2: 2025-2026 → 2026-2027
        AnneeAcademique annee2 = anneeAcademiqueService.passerAAnneeSuivante("2026-2027");
        assertEquals("2026-2027", annee2.getAnnee());
        assertTrue(annee2.getEstCourante());
        
        // THEN: Vérifier que seule la dernière année est courante
        assertFalse(annee1.getEstCourante()); // Rechargée de la base
        
        // WHEN/THEN: Tentative de revenir en arrière (doit échouer)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            anneeAcademiqueService.passerAAnneeSuivante("2025-2026");
        });
        assertTrue(exception.getMessage().contains("Transition non autorisée"));
    }
}