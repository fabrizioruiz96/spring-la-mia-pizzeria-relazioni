package it.exercise.spring_la_mia_pizzeria_relazioni.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import it.exercise.spring_la_mia_pizzeria_relazioni.model.Promo;
import it.exercise.spring_la_mia_pizzeria_relazioni.repository.PromoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/promos")
public class PromoController {

    @Autowired
    private PromoRepository promoRepository;

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("promo") Promo formPromo,
        BindingResult bindingResult,
        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", false);
            model.addAttribute("promo", formPromo);
            return "/promos/edit";
        }

        promoRepository.save(formPromo);

        return "redirect:/pizzas/show/" + formPromo.getPizza().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Promo promo = promoRepository.findById(id).get();
        model.addAttribute("promo", promo);
        model.addAttribute("editMode", true);
        return "/promos/edit";
    }

    @PostMapping("/edit/{id}")
    public String doEdit(@Valid @ModelAttribute("promo") Promo promo,
        BindingResult bindingResult,
        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", true);
            model.addAttribute("promo", promo);
            return "/promos/edit";
        }

        promoRepository.save(promo);
        return "redirect:/pizzas/show/" + promo.getPizza().getId();
        }
}
