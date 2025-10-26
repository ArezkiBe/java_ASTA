# 📊 ANALYSE CRITIQUE DES STRATÉGIES DE PERSISTANCE

## 🎯 EXIGENCE TECHNIQUE 6 - PERSISTANCE

### **a) Mélange de requêtes prédéfinies et explicites (JPQL)**
### **b) Une seule requête SQL standard avec analyse critique**

---

## ✅ IMPLÉMENTATION RÉALISÉE

### 🔧 **Requêtes Prédéfinies (Spring Data JPA)**
```java
// Exemples utilisés dans le projet
List<Apprenti> findByEstArchiveFalseOrderByNomAscPrenomAsc();
List<Apprenti> findByTuteurEnseignantIdAndEstArchiveFalse(Integer tuteurId);
List<Apprenti> findByNomContainingIgnoreCaseAndEstArchiveFalse(String nom);
Optional<AnneeAcademique> findByEstCouranteTrue();
List<AnneeAcademique> findAllByOrderByAnneeDesc();
```

### 🔍 **Requêtes JPQL Explicites**
```java
// Recherche complexe avec JOIN
@Query("SELECT a FROM Apprenti a JOIN a.entreprise e WHERE e.id = :entrepriseId AND a.estArchive = false ORDER BY a.nom, a.prenom")
List<Apprenti> findByEntrepriseIdAndEstArchiveFalse(Integer entrepriseId);

// Recherche avancée multi-critères
@Query("SELECT a FROM Apprenti a LEFT JOIN a.entreprise e LEFT JOIN a.mission m LEFT JOIN a.anneeAcademique aa " +
       "WHERE (:nom IS NULL OR LOWER(a.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) " +
       "AND (:entreprise IS NULL OR LOWER(e.raisonSociale) LIKE LOWER(CONCAT('%', :entreprise, '%'))) " +
       "AND a.estArchive = false ORDER BY a.nom, a.prenom")
List<Apprenti> rechercheAvancee(String nom, String entreprise, String mission, String annee);
```

### 🗃️ **Requête SQL Native (UNIQUE)**
```sql
@Query(value = """
    SELECT a.programme, 
           aa.annee,
           COUNT(*) as nombre_apprentis,
           COUNT(CASE WHEN a.est_archive = 1 THEN 1 END) as nombre_archives,
           COUNT(CASE WHEN a.est_archive = 0 THEN 1 END) as nombre_actifs,
           e.raison_sociale as entreprise_principale
    FROM apprenti a
    INNER JOIN annee_academique aa ON a.annee_academique_id = aa.id
    LEFT JOIN entreprise e ON a.entreprise_id = e.id
    WHERE a.programme IN ('I1', 'I2', 'I3', 'M2-PRO')
    GROUP BY a.programme, aa.annee, e.raison_sociale
    HAVING COUNT(*) >= 1
    ORDER BY aa.annee DESC, a.programme ASC, nombre_apprentis DESC
    """, nativeQuery = true)
List<Object[]> getStatistiquesApprentisParProgrammeEtAnnee();
```

---

## 🔍 ANALYSE CRITIQUE DE LA REQUÊTE SQL NATIVE

### ✅ **AVANTAGES**

#### **1. Performance Optimale**
- **JOIN complexes optimisés** : La base de données peut optimiser directement les jointures
- **Agrégations performantes** : `COUNT()`, `GROUP BY`, `HAVING` sont natifs SQL
- **Indexation directe** : Utilise les index de la base sans couche d'abstraction

#### **2. Fonctionnalités SQL Avancées**
- **CASE WHEN** : Comptage conditionnel impossible en JPQL simple
- **GROUP BY complexe** : Groupement sur plusieurs colonnes avec agrégations
- **HAVING** : Filtrage post-agrégation plus naturel en SQL

#### **3. Lisibilité pour Requêtes Complexes**
- **Syntaxe familière** : Les DBA peuvent facilement comprendre et optimiser
- **Requête métier claire** : L'intention (statistiques) est évidente

### ❌ **INCONVÉNIENTS**

#### **1. Portabilité Limitée**
- **Dépendance MariaDB** : Syntaxe spécifique (ex: `CASE WHEN`, fonctions)
- **Migration difficile** : Changement de SGBD nécessiterait réécriture
- **Tests complexes** : Nécessite une base réelle ou H2 compatible

#### **2. Maintenance Risquée**
- **Pas de validation compile-time** : Erreurs découvertes à l'exécution
- **Evolution schema** : Renommage colonnes casse la requête
- **Refactoring impossible** : IDE ne peut pas renommer automatiquement

#### **3. Intégration JPA Limitée**
- **Type de retour Object[]** : Nécessite mapping manuel
- **Pas de lazy loading** : Pas de gestion automatique des relations
- **Cache JPA ignoré** : Pas de mise en cache automatique

### 🔄 **ALTERNATIVE JPQL POSSIBLE**
```java
// Version JPQL (plus verbeux mais portable)
@Query("SELECT a.programme, aa.annee, COUNT(a), " +
       "SUM(CASE WHEN a.estArchive = true THEN 1 ELSE 0 END), " +
       "SUM(CASE WHEN a.estArchive = false THEN 1 ELSE 0 END), " +
       "e.raisonSociale " +
       "FROM Apprenti a " +
       "INNER JOIN a.anneeAcademique aa " +
       "LEFT JOIN a.entreprise e " +
       "WHERE a.programme IN ('I1', 'I2', 'I3', 'M2-PRO') " +
       "GROUP BY a.programme, aa.annee, e.raisonSociale " +
       "HAVING COUNT(a) >= 1 " +
       "ORDER BY aa.annee DESC, a.programme ASC, COUNT(a) DESC")
List<Object[]> getStatistiquesApprentisParProgrammeEtAnneeJPQL();
```

---

## 🎯 RECOMMANDATIONS PROJET

### **Utilisation Actuelle Justifiée**
1. **Requête métier complexe** → SQL natif approprié
2. **Requêtes simples** → Spring Data JPA (findBy...)  
3. **Recherches avec JOIN** → JPQL explicite
4. **Statistiques/Rapports** → SQL natif acceptable

### **Bonnes Pratiques Appliquées**
- ✅ **Une seule requête SQL** (respect strict de la consigne)
- ✅ **Requêtes prédéfinies** pour CRUD simple
- ✅ **JPQL pour logique métier** avec paramètres
- ✅ **SQL natif uniquement** pour cas complexes impossibles en JPQL

### **Améliorations Possibles**
1. **DTO personnalisé** au lieu d'Object[] pour le retour SQL
2. **@SqlResultSetMapping** pour mapping typé
3. **Tests unitaires** spécifiques pour la requête SQL
4. **Documentation** des index recommandés

---

## 📊 CONCLUSION

L'usage **d'une unique requête SQL native** est **justifié** pour cette requête de statistiques complexes. Elle offre des performances optimales pour un cas métier spécifique tout en respectant la contrainte pédagogique. 

Le **mélange avec JPQL et requêtes prédéfinies** assure un équilibre entre **performance**, **maintenabilité** et **portabilité** selon le contexte d'usage.

**Conformité exigence 6 : ✅ RESPECTÉE**