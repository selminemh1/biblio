package com.library.controller;

import com.library.model.Member;
import com.library.service.LoanService;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final LoanService loanService;

    @GetMapping
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "members/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("statuses", Member.MemberStatus.values());
        return "members/form";
    }

    @PostMapping
    public String createMember(@Valid @ModelAttribute Member member,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", Member.MemberStatus.values());
            return "members/form";
        }

        memberService.save(member);
        redirectAttributes.addFlashAttribute("successMessage", "Membre ajouté avec succès!");
        return "redirect:/members";
    }

    @GetMapping("/{id}")
    public String viewMember(@PathVariable Long id, Model model) {
        return memberService.findById(id)
            .map(member -> {
                model.addAttribute("member", member);
                model.addAttribute("activeLoans", loanService.findActiveLoansByMemberId(id));
                model.addAttribute("allLoans", loanService.findByMemberId(id));
                return "members/view";
            })
            .orElse("redirect:/members");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return memberService.findById(id)
            .map(member -> {
                model.addAttribute("member", member);
                model.addAttribute("statuses", Member.MemberStatus.values());
                return "members/form";
            })
            .orElse("redirect:/members");
    }

    @PostMapping("/{id}")
    public String updateMember(@PathVariable Long id,
                              @Valid @ModelAttribute Member member,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", Member.MemberStatus.values());
            return "members/form";
        }

        member.setId(id);
        memberService.save(member);
        redirectAttributes.addFlashAttribute("successMessage", "Membre mis à jour avec succès!");
        return "redirect:/members";
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        memberService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Membre supprimé avec succès!");
        return "redirect:/members";
    }

    @GetMapping("/search")
    public String searchMembers(@RequestParam String query, Model model) {
        model.addAttribute("members", memberService.searchByName(query));
        model.addAttribute("searchQuery", query);
        return "members/list";
    }
}
