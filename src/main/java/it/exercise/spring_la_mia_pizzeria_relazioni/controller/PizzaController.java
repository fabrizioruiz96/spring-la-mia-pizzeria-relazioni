package it.exercise.spring_la_mia_pizzeria_relazioni.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.exercise.spring_la_mia_pizzeria_relazioni.model.Pizza;
import it.exercise.spring_la_mia_pizzeria_relazioni.model.Promo;
import it.exercise.spring_la_mia_pizzeria_relazioni.repository.IngredientRepository;
import it.exercise.spring_la_mia_pizzeria_relazioni.repository.PizzaRepository;
import it.exercise.spring_la_mia_pizzeria_relazioni.repository.PromoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired 
    private IngredientRepository ingredientRepository;

    @Autowired
    private PromoRepository promoRepository;

    @GetMapping
    public String index(Model model, @RequestParam(name = "keyword", required = false) String name) {
        List<Pizza> result;
        if (name != null && !name.isBlank()) {
            result = pizzaRepository.findByNameContainingIgnoreCase(name);
        } else {
            result = pizzaRepository.findAll();
        }
        model.addAttribute("list", result);
        return "pizzas/index";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable("id") Integer id, Model model) {
        Optional<Pizza> optPizza = pizzaRepository.findById(id);
        if (optPizza.isPresent()) {
            model.addAttribute("pizza", optPizza.get());
            return "/pizzas/show";
        }

        model.addAttribute("errorCause", "Nessuna pizza trovata con questo id: " + id);
        model.addAttribute("errorMessage", "Errore di ricerca della pizza");
        return "/error_pages/generalError";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza());
        model.addAttribute("ingredientList", ingredientRepository.findAll());
        return "/pizzas/create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "/pizzas/create";
        }

        pizzaRepository.save(formPizza);

        redirectAttributes.addFlashAttribute("successMessage", "Pizza creata!");
        return "redirect:/pizzas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("pizza", pizzaRepository.findById(id).get());
        model.addAttribute("ingredientList", ingredientRepository.findAll());
        return "pizzas/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute("pizza") Pizza formPizza,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "/pizzas/edit";
        }

        pizzaRepository.save(formPizza);

        redirectAttributes.addFlashAttribute("successMessage", "Pizza modificata!");
        return "redirect:/pizzas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {

        Pizza pizza = pizzaRepository.findById(id).get();

        for (Promo p : pizza.getPromos()) {
            promoRepository.deleteById(p.getId());
        }

        pizzaRepository.deleteById(id);

        return "redirect:/pizzas";
    }

    @GetMapping("/{id}/promo")
    public String promo(@PathVariable("id") Integer id, Model model) {
        
        Promo promo = new Promo();
        promo.setPizza(pizzaRepository.findById(id).get());

        model.addAttribute("promo", promo);
        model.addAttribute("editMode", false);

        return "/promos/edit";
    }
    
}
