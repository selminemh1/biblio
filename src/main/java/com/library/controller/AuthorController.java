package com.library.controller;

import com.library.model.Author;
import com.library.service.AuthorService;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "authors/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/form";
    }

    @PostMapping
    public String createAuthor(@Valid @ModelAttribute Author author,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "authors/form";
        }

        authorService.save(author);
        redirectAttributes.addFlashAttribute("successMessage", "Auteur ajouté avec succès!");
        return "redirect:/authors";
    }

    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable Long id, Model model) {
        return authorService.findById(id)
            .map(author -> {
                model.addAttribute("author", author);
                model.addAttribute("books", bookService.findByAuthorId(id));
                return "authors/view";
            })
            .orElse("redirect:/authors");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return authorService.findById(id)
            .map(author -> {
                model.addAttribute("author", author);
                return "authors/form";
            })
            .orElse("redirect:/authors");
    }

    @PostMapping("/{id}")
    public String updateAuthor(@PathVariable Long id,
                              @Valid @ModelAttribute Author author,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "authors/form";
        }

        author.setId(id);
        authorService.save(author);
        redirectAttributes.addFlashAttribute("successMessage", "Auteur mis à jour avec succès!");
        return "redirect:/authors";
    }

    @PostMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        authorService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Auteur supprimé avec succès!");
        return "redirect:/authors";
    }

    @GetMapping("/search")
    public String searchAuthors(@RequestParam String query, Model model) {
        model.addAttribute("authors", authorService.searchByName(query));
        model.addAttribute("searchQuery", query);
        return "authors/list";
    }
}
