package com.library.repository;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    List<Loan> findByMember(Member member);
    
    List<Loan> findByMemberId(Long memberId);
    
    List<Loan> findByBook(Book book);
    
    List<Loan> findByBookId(Long bookId);
    
    List<Loan> findByStatus(Loan.LoanStatus status);
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :today")
    List<Loan> findOverdueLoans(@Param("today") LocalDate today);
    
    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.book LEFT JOIN FETCH l.member WHERE l.status = 'ACTIVE'")
    List<Loan> findActiveLoansWithDetails();
    
    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.book LEFT JOIN FETCH l.member")
    List<Loan> findAllWithDetails();
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'")
    long countActiveLoans();
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < CURRENT_DATE")
    long countOverdueLoans();
    
    @Query("SELECT l FROM Loan l WHERE l.member.id = :memberId AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByMemberId(@Param("memberId") Long memberId);
}
