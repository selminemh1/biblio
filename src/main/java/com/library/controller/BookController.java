package com.library.controller;

import com.library.model.Book;
import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAllWithDetails());
        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("authors", authorService.findAll());
        return "books/form";
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute Book book,
                            BindingResult result,
                            @RequestParam(value = "authorIds", required = false) Long[] authorIds,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("authors", authorService.findAll());
            return "books/form";
        }

        if (authorIds != null) {
            for (Long authorId : authorIds) {
                authorService.findById(authorId).ifPresent(author -> book.getAuthors().add(author));
            }
        }

        bookService.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Livre ajouté avec succès!");
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        return bookService.findByIdWithDetails(id)
            .map(book -> {
                model.addAttribute("book", book);
                return "books/view";
            })
            .orElse("redirect:/books");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return bookService.findByIdWithDetails(id)
            .map(book -> {
                model.addAttribute("book", book);
                model.addAttribute("categories", categoryService.findAll());
                model.addAttribute("authors", authorService.findAll());
                return "books/form";
            })
            .orElse("redirect:/books");
    }

    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id,
                            @Valid @ModelAttribute Book book,
                            BindingResult result,
                            @RequestParam(value = "authorIds", required = false) Long[] authorIds,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("authors", authorService.findAll());
            return "books/form";
        }

        book.setId(id);
        book.getAuthors().clear();
        
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                authorService.findById(authorId).ifPresent(author -> book.getAuthors().add(author));
            }
        }

        bookService.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Livre mis à jour avec succès!");
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Livre supprimé avec succès!");
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam String query, Model model) {
        model.addAttribute("books", bookService.findByTitle(query));
        model.addAttribute("searchQuery", query);
        return "books/list";
    }

    @GetMapping("/category/{categoryId}")
    public String booksByCategory(@PathVariable Long categoryId, Model model) {
        model.addAttribute("books", bookService.findByCategoryId(categoryId));
        categoryService.findById(categoryId).ifPresent(category -> 
            model.addAttribute("categoryName", category.getName()));
        return "books/list";
    }
}
