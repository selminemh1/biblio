# Système de Gestion de Bibliothèque

Un système de gestion de bibliothèque complet développé avec **Spring Boot MVC**, **Thymeleaf** et **Bootstrap 5**.

## Fonctionnalités

- **Gestion des Livres** : CRUD complet, recherche, filtrage par catégorie
- **Gestion des Auteurs** : Profils d'auteurs avec leurs œuvres
- **Gestion des Catégories** : Organisation des livres par genre
- **Gestion des Membres** : Inscription et suivi des membres
- **Gestion des Emprunts** : Prêts, retours, suivi des retards

## Architecture de la Base de Données

### Schéma des Relations (Cardinalités)

```
┌─────────────┐       ┌─────────────┐
│   AUTHOR    │──────<│ BOOK_AUTHORS│>──────│    BOOK     │
│             │  N:M  │ (table pivot)│       │             │
└─────────────┘       └─────────────┘       └─────────────┘
                                                   │
                                                   │ N:1
                                                   ▼
                                            ┌─────────────┐
                                            │  CATEGORY   │
                                            └─────────────┘

┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   MEMBER    │──────<│    LOAN     │>──────│    BOOK     │
│             │  1:N  │             │  N:1  │             │
└─────────────┘       └─────────────┘       └─────────────┘
```

### Relations détaillées

| Relation | Cardinalité | Description |
|----------|-------------|-------------|
| Author ↔ Book | Many-to-Many | Un auteur peut écrire plusieurs livres, un livre peut avoir plusieurs auteurs |
| Category → Book | One-to-Many | Une catégorie contient plusieurs livres |
| Member → Loan | One-to-Many | Un membre peut avoir plusieurs emprunts |
| Book → Loan | One-to-Many | Un livre peut être emprunté plusieurs fois |

## Prérequis

- **Java 17** ou supérieur
- **Maven 3.6+**

## Installation et Lancement

### 1. Cloner ou télécharger le projet

```bash
cd "c:\Users\user\Desktop\projet wejden\library-management"
```

### 2. Compiler le projet

```bash
mvn clean install
```

### 3. Lancer l'application

```bash
mvn spring-boot:run
```

### 4. Accéder à l'application

Ouvrez votre navigateur et allez sur : **http://localhost:8080**

## Technologies Utilisées

| Technologie | Version | Usage |
|-------------|---------|-------|
| Spring Boot | 3.2.0 | Framework principal |
| Spring Data JPA | - | Persistance des données |
| Thymeleaf | - | Moteur de templates |
| H2 Database | - | Base de données en mémoire |
| Bootstrap | 5.3.2 | Framework CSS |
| Lombok | - | Réduction du boilerplate |

## Structure du Projet

```
library-management/
├── src/main/java/com/library/
│   ├── config/           # Configuration et initialisation
│   ├── controller/       # Contrôleurs MVC
│   ├── model/            # Entités JPA
│   ├── repository/       # Repositories Spring Data
│   └── service/          # Services métier
├── src/main/resources/
│   ├── templates/        # Vues Thymeleaf
│   │   ├── fragments/    # Fragments réutilisables
│   │   ├── books/        # Pages livres
│   │   ├── authors/      # Pages auteurs
│   │   ├── categories/   # Pages catégories
│   │   ├── members/      # Pages membres
│   │   └── loans/        # Pages emprunts
│   └── application.properties
└── pom.xml
```

## Données de Test

L'application est préchargée avec des données de test :
- **5 Catégories** : Roman, Science-Fiction, Histoire, Informatique, Philosophie
- **5 Auteurs** : Victor Hugo, Albert Camus, Isaac Asimov, J.K. Rowling, Robert C. Martin
- **6 Livres** : Les Misérables, L'Étranger, Fondation, Harry Potter, Clean Code, Le Mythe de Sisyphe
- **4 Membres** : Marie Dupont, Jean Martin, Sophie Bernard, Pierre Durand
- **4 Emprunts** : Dont 1 en retard et 1 retourné

## Console H2 (Base de données)

Accédez à la console H2 pour visualiser les données :
- URL : **http://localhost:8080/h2-console**
- JDBC URL : `jdbc:h2:mem:librarydb`
- Username : `sa`
- Password : *(vide)*

## Captures d'écran

L'interface inclut :
- 📊 Tableau de bord avec statistiques
- 📚 Liste des livres avec filtres
- 👥 Gestion des membres
- 🔄 Suivi des emprunts avec alertes de retard
- 🎨 Design moderne et responsive

## Auteur

Projet créé pour démonstration Spring Boot MVC avec JPA.
