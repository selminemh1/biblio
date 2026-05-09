package com.library.controller;

import com.library.model.Category;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final BookService bookService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/form";
        }

        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie ajoutée avec succès!");
        return "redirect:/categories";
    }

    @GetMapping("/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        return categoryService.findById(id)
            .map(category -> {
                model.addAttribute("category", category);
                model.addAttribute("books", bookService.findByCategoryId(id));
                return "categories/view";
            })
            .orElse("redirect:/categories");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return categoryService.findById(id)
            .map(category -> {
                model.addAttribute("category", category);
                return "categories/form";
            })
            .orElse("redirect:/categories");
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                @Valid @ModelAttribute Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/form";
        }

        category.setId(id);
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie mise à jour avec succès!");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie supprimée avec succès!");
        return "redirect:/categories";
    }
}
