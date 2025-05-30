package pizzeria.spring_la_mia_pizzeria_crud.controller;

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

import jakarta.validation.Valid;
import pizzeria.spring_la_mia_pizzeria_crud.model.OfferteSpeciali;
import pizzeria.spring_la_mia_pizzeria_crud.model.Pizza;
import pizzeria.spring_la_mia_pizzeria_crud.repository.IngredientiRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.OfferteRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.PizzaRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.QuantitaPizzeRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.UserRepository;
import pizzeria.spring_la_mia_pizzeria_crud.service.PizzaService;

@Controller
@RequestMapping

public class PizzaController {

    private final UserRepository userRepository;

    private final QuantitaPizzeRepository quantitaPizzeRepository;

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private final OfferteRepository offerteRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private IngredientiRepository ingredientiRepository;

    PizzaController(OfferteRepository offerteRepository, QuantitaPizzeRepository quantitaPizzeRepository, UserRepository userRepository) {
        this.offerteRepository = offerteRepository;
        this.quantitaPizzeRepository = quantitaPizzeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String primaPagina(Model model) {
        return "pizze/primaPagina";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout, Model model) {
                if(logout != null){
                    model.addAttribute("logoutMessage", "Logout riuscito!");
                    return "pizze/login";
                }
        if (error != null) {
            model.addAttribute("errorMessageLogin", "Username o password errati!");
            return "pizze/login";
        }
        if (userRepository.count() == 0) {
            model.addAttribute("errorUser", "Il sito è in manutenzione riprova più tardi!");
            return "pizze/login";
        }
        return "pizze/login";
    }

    @GetMapping("/loading")
    public String loading(Model model) {
        return "pizze/loading";
    }

    @GetMapping("/pizze")
    public String index(Model model, @RequestParam(name = "keyword", required = false) String findPizza) {
        model.addAttribute("pizzaList", pizzaService.findPizzaList(findPizza));
        return "pizze/index";
    }

    @GetMapping("pizze/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Optional<Pizza> optionPizza = pizzaRepository.findById(id);
        if (optionPizza.isPresent()) {
            model.addAttribute("pizza", pizzaRepository.findById(id).get());
            return "pizze/show";
        }
        model.addAttribute("errorCause", "La pizza da te cercata con id " + id + " non esiste");
        return "pizze/error/error";
    }

    @GetMapping("/contatti")
    public String contatti() {
        return "pizze/contatti";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza());
        model.addAttribute("listaIngredienti", ingredientiRepository.findAll());
        return "pizze/create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("listaIngredienti", ingredientiRepository.findAll());
            return "pizze/create";
        }

        pizzaRepository.save(formPizza);
        return "redirect:/pizze";
    }

    @GetMapping("/modifica/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {

        model.addAttribute("modificaPizza", pizzaRepository.findById(id).get());
        model.addAttribute("listaIngredienti", ingredientiRepository.findAll());

        return "/pizze/modificaPizza";
    }

    @PostMapping("/modifica/{id}")
    public String update(
            @Valid @ModelAttribute("modificaPizza") Pizza formPizza,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("listaIngredienti", ingredientiRepository.findAll());
            return "/pizze/modificaPizza";
        }
        pizzaRepository.save(formPizza);

        return "redirect:/pizze";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        pizzaService.deleteById(id);
        return "redirect:/pizze";
    }

    @GetMapping("/{id}/offerte")
    public String editOfferte(@PathVariable Long id, Model model) {
        OfferteSpeciali offerteSpeciali = new OfferteSpeciali();
        offerteSpeciali.setPizza(pizzaRepository.findById(id).get());
        model.addAttribute("offerte", offerteSpeciali);
        model.addAttribute("editMode", false);
        return "offerte/edit";
    }

}
