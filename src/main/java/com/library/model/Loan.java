package com.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"book", "member"})
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Le livre est obligatoire")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull(message = "Le membre est obligatoire")
    private Member member;

    @NotNull(message = "La date d'emprunt est obligatoire")
    private LocalDate loanDate;

    @NotNull(message = "La date de retour prévue est obligatoire")
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;

    private String notes;

    @PrePersist
    public void prePersist() {
        if (loanDate == null) {
            loanDate = LocalDate.now();
        }
        if (dueDate == null) {
            dueDate = loanDate.plusDays(14);
        }
    }

    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public long getDaysRemaining() {
        if (status != LoanStatus.ACTIVE) return 0;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return Math.max(0, days);
    }

    public enum LoanStatus {
        ACTIVE, RETURNED, OVERDUE
    }
}
