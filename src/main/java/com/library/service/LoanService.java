package com.library.service;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public List<Loan> findAllWithDetails() {
        return loanRepository.findAllWithDetails();
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }

    public Loan createLoan(Book book, Member member, LocalDate dueDate) {
        if (!book.isAvailable()) {
            throw new IllegalStateException("Le livre n'est pas disponible");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusDays(14));
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        bookService.decrementAvailableCopies(book);

        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("Emprunt non trouvé"));

        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new IllegalStateException("Le livre a déjà été retourné");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(Loan.LoanStatus.RETURNED);

        bookService.incrementAvailableCopies(loan.getBook());

        return loanRepository.save(loan);
    }

    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

    public List<Loan> findByMemberId(Long memberId) {
        return loanRepository.findByMemberId(memberId);
    }

    public List<Loan> findByBookId(Long bookId) {
        return loanRepository.findByBookId(bookId);
    }

    public List<Loan> findActiveLoans() {
        return loanRepository.findByStatus(Loan.LoanStatus.ACTIVE);
    }

    public List<Loan> findActiveLoansWithDetails() {
        return loanRepository.findActiveLoansWithDetails();
    }

    public List<Loan> findOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now());
    }

    public long countActiveLoans() {
        return loanRepository.countActiveLoans();
    }

    public long countOverdueLoans() {
        return loanRepository.countOverdueLoans();
    }

    public List<Loan> findActiveLoansByMemberId(Long memberId) {
        return loanRepository.findActiveLoansByMemberId(memberId);
    }
}
