package com.library.config;

import com.library.model.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    @Override
    public void run(String... args) {
        // Ne créer les données que si la base est vide
        if (categoryRepository.count() > 0) {
            System.out.println("===========================================");
            System.out.println("  Base de données déjà initialisée");
            System.out.println("  " + bookRepository.count() + " livres, " + memberRepository.count() + " membres");
            System.out.println("===========================================");
            return;
        }

        // Création des catégories
        Category roman = createCategory("Roman", "Romans et fiction littéraire");
        Category scienceFiction = createCategory("Science-Fiction", "Science-fiction et fantasy");
        Category histoire = createCategory("Histoire", "Livres d'histoire et biographies");
        Category informatique = createCategory("Informatique", "Programmation et technologies");
        Category philosophie = createCategory("Philosophie", "Philosophie et pensée");

        // Création des auteurs
        Author victorHugo = createAuthor("Victor", "Hugo", "Français", "Écrivain romantique français, auteur des Misérables");
        Author albertCamus = createAuthor("Albert", "Camus", "Français", "Écrivain et philosophe français, prix Nobel de littérature");
        Author isaacAsimov = createAuthor("Isaac", "Asimov", "Américain", "Auteur de science-fiction prolifique");
        Author jkRowling = createAuthor("J.K.", "Rowling", "Britannique", "Auteure de la saga Harry Potter");
        Author robertMartin = createAuthor("Robert C.", "Martin", "Américain", "Expert en développement logiciel, auteur de Clean Code");

        // Création des livres avec auteurs
        Book lesMiserables = createBookWithAuthors("Les Misérables", "978-2-07-040850-4", roman, 
            "Chef-d'œuvre de la littérature française", LocalDate.of(1862, 1, 1), 1900, 5, victorHugo);

        Book lEtranger = createBookWithAuthors("L'Étranger", "978-2-07-036024-8", roman,
            "Roman philosophique sur l'absurdité de la vie", LocalDate.of(1942, 1, 1), 185, 3, albertCamus);

        Book fondation = createBookWithAuthors("Fondation", "978-2-07-041539-7", scienceFiction,
            "Premier tome de la célèbre saga de science-fiction", LocalDate.of(1951, 1, 1), 416, 4, isaacAsimov);

        Book harryPotter = createBookWithAuthors("Harry Potter à l'école des sorciers", "978-2-07-054127-0", scienceFiction,
            "Premier tome des aventures du jeune sorcier", LocalDate.of(1997, 6, 26), 320, 6, jkRowling);

        Book cleanCode = createBookWithAuthors("Clean Code", "978-0-13-235088-4", informatique,
            "Guide des bonnes pratiques en programmation", LocalDate.of(2008, 8, 1), 464, 3, robertMartin);

        Book leMythe = createBookWithAuthors("Le Mythe de Sisyphe", "978-2-07-032288-8", philosophie,
            "Essai sur l'absurde", LocalDate.of(1942, 1, 1), 187, 2, albertCamus);

        // Création des membres
        Member marie = createMember("Marie", "Dupont", "marie.dupont@email.com", "+33 6 12 34 56 78", "123 Rue de Paris, 75001 Paris");
        Member jean = createMember("Jean", "Martin", "jean.martin@email.com", "+33 6 98 76 54 32", "456 Avenue des Champs, 69001 Lyon");
        Member sophie = createMember("Sophie", "Bernard", "sophie.bernard@email.com", "+33 6 11 22 33 44", "789 Boulevard Central, 13001 Marseille");
        Member pierre = createMember("Pierre", "Durand", "pierre.durand@email.com", "+33 6 55 66 77 88", "321 Rue du Commerce, 31000 Toulouse");

        // Création de quelques emprunts
        createLoan(lesMiserables, marie, LocalDate.now().minusDays(7), LocalDate.now().plusDays(7));
        createLoan(fondation, jean, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6)); // En retard
        createLoan(cleanCode, sophie, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        
        // Un emprunt retourné
        Loan returnedLoan = createLoan(harryPotter, pierre, LocalDate.now().minusDays(14), LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now().minusDays(2));
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanRepository.save(returnedLoan);
        
        // Incrémenter le livre retourné
        harryPotter.setAvailableCopies(harryPotter.getAvailableCopies() + 1);
        bookRepository.save(harryPotter);

        System.out.println("===========================================");
        System.out.println("  Données de test initialisées avec succès!");
        System.out.println("  - 5 Catégories");
        System.out.println("  - 5 Auteurs");
        System.out.println("  - 6 Livres");
        System.out.println("  - 4 Membres");
        System.out.println("  - 4 Emprunts");
        System.out.println("===========================================");
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private Author createAuthor(String firstName, String lastName, String nationality, String biography) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setNationality(nationality);
        author.setBiography(biography);
        return authorRepository.save(author);
    }

    private Book createBookWithAuthors(String title, String isbn, Category category, String description, 
                           LocalDate publishedDate, int pages, int copies, Author... authors) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setDescription(description);
        book.setPublishedDate(publishedDate);
        book.setPages(pages);
        book.setTotalCopies(copies);
        book.setAvailableCopies(copies);
        book.setAuthors(new HashSet<>(Arrays.asList(authors)));
        return bookRepository.save(book);
    }

    private Member createMember(String firstName, String lastName, String email, String phone, String address) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setPhone(phone);
        member.setAddress(address);
        member.setMembershipDate(LocalDate.now().minusMonths((long)(Math.random() * 12)));
        member.setStatus(Member.MemberStatus.ACTIVE);
        return memberRepository.save(member);
    }

    private Loan createLoan(Book book, Member member, LocalDate loanDate, LocalDate dueDate) {
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        // Décrémenter les copies disponibles
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }
}
