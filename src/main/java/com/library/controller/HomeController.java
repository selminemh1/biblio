package com.library.controller;

import com.library.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final MemberService memberService;
    private final LoanService loanService;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalBooks", bookService.count());
        model.addAttribute("availableCopies", bookService.countAvailableCopies());
        model.addAttribute("totalMembers", memberService.count());
        model.addAttribute("activeMembers", memberService.countActiveMembers());
        model.addAttribute("activeLoans", loanService.countActiveLoans());
        model.addAttribute("overdueLoans", loanService.countOverdueLoans());
        model.addAttribute("totalAuthors", authorService.count());
        model.addAttribute("totalCategories", categoryService.count());
        model.addAttribute("recentLoans", loanService.findActiveLoansWithDetails());
        model.addAttribute("availableBooks", bookService.findAvailableBooks());
        return "index";
    }
}
