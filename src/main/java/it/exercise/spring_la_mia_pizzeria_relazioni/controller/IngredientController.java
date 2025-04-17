package it.exercise.spring_la_mia_pizzeria_relazioni.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.exercise.spring_la_mia_pizzeria_relazioni.model.Ingredient;
import it.exercise.spring_la_mia_pizzeria_relazioni.model.Pizza;
import it.exercise.spring_la_mia_pizzeria_relazioni.repository.IngredientRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {
    
    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("list", ingredientRepository.findAll());
        model.addAttribute("ingredientObj", new Ingredient());
        return "/ingredients/index";
    }

    @PostMapping("/create")
    public String store(
        @Valid @ModelAttribute("ingredientObj") Ingredient ingredient,
        BindingResult bindingResult) {

        if (!bindingResult.hasErrors()) {
            ingredientRepository.save(ingredient);
        }

        return "redirect:/ingredients";
    }

    @PostMapping("/delete/{id}") 
    public String delete(@PathVariable("id") Integer id, Model model) {

        Ingredient ingredient = ingredientRepository.findById(id).get();

        for(Pizza pizza : ingredient.getPizzas()) {
            pizza.getIngredients().remove(ingredient);
        }

        ingredientRepository.deleteById(id);
        return "redirect:/ingredients";
    }

}
