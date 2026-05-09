package com.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"authors", "loans", "category"})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "L'ISBN est obligatoire")
    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(length = 2000)
    private String description;

    private LocalDate publishedDate;

    @Positive(message = "Le nombre de pages doit être positif")
    private Integer pages;

    @NotNull(message = "Le nombre de copies est obligatoire")
    @Positive(message = "Le nombre de copies doit être positif")
    private Integer totalCopies;

    private Integer availableCopies;

    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull(message = "La catégorie est obligatoire")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (availableCopies == null) {
            availableCopies = totalCopies;
        }
    }

    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }
}
