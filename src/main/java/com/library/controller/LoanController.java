package com.library.controller;

import com.library.model.Loan;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;

    @GetMapping
    public String listLoans(Model model) {
        model.addAttribute("loans", loanService.findAllWithDetails());
        return "loans/list";
    }

    @GetMapping("/active")
    public String listActiveLoans(Model model) {
        model.addAttribute("loans", loanService.findActiveLoansWithDetails());
        model.addAttribute("showActiveOnly", true);
        return "loans/list";
    }

    @GetMapping("/overdue")
    public String listOverdueLoans(Model model) {
        model.addAttribute("loans", loanService.findOverdueLoans());
        model.addAttribute("showOverdueOnly", true);
        return "loans/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("loan", new Loan());
        model.addAttribute("books", bookService.findAvailableBooks());
        model.addAttribute("members", memberService.findAll());
        return "loans/form";
    }

    @PostMapping
    public String createLoan(@RequestParam Long bookId,
                            @RequestParam Long memberId,
                            @RequestParam(required = false) 
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                            RedirectAttributes redirectAttributes) {
        try {
            var book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Livre non trouvé"));
            var member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));

            loanService.createLoan(book, member, dueDate);
            redirectAttributes.addFlashAttribute("successMessage", "Emprunt créé avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/loans";
    }

    @GetMapping("/{id}")
    public String viewLoan(@PathVariable Long id, Model model) {
        return loanService.findById(id)
            .map(loan -> {
                model.addAttribute("loan", loan);
                return "loans/view";
            })
            .orElse("redirect:/loans");
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            loanService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Livre retourné avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/loans";
    }

    @PostMapping("/{id}/delete")
    public String deleteLoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Emprunt supprimé avec succès!");
        return "redirect:/loans";
    }
}
