package pizzeria.spring_la_mia_pizzeria_crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pizzeria.spring_la_mia_pizzeria_crud.dto.RecordDtoView;
import pizzeria.spring_la_mia_pizzeria_crud.model.Pizza;
import pizzeria.spring_la_mia_pizzeria_crud.model.RecordShop;
import pizzeria.spring_la_mia_pizzeria_crud.model.Shop;
import pizzeria.spring_la_mia_pizzeria_crud.repository.CarrelloRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.PizzaRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.ShopRecordRepository;
import pizzeria.spring_la_mia_pizzeria_crud.repository.UserRepository;

@Controller
public class ShopController {

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private CarrelloRepository shopRepository;

    @Autowired
    private ShopRecordRepository recordShopRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/addShop/{idPizza}")
    public String addShop(
            @PathVariable Long idPizza,
            @RequestParam Long shopId,
            @RequestParam Integer quantitaPizzaCarrello,
            Model model,
            RedirectAttributes redirectAttributes) {

        //controllo quantità carrello
        if (quantitaPizzaCarrello == null || quantitaPizzaCarrello <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hai inserito una quantità uguale o inferiore a zero");
            return "redirect:/pizze";
        }
        List<RecordDtoView> carrello = recordShopRepository.findCarrelloView(shopId);
        Pizza pizza = pizzaRepository.findById(idPizza).get();
        //pizza NO null
        if (pizza == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pizza non trovata");
            return "redirect:/pizze";
        }
        for (RecordDtoView record : carrello) {
            if (pizza.getId().equals(record.getIdPizza())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Pizza non aggiunta, modifica la quantità nel carrello");
                return "redirect:/pizze";
            }
        }
        redirectAttributes.addFlashAttribute("successMessage", "La pizza è stata aggiunta nel carrello");
        Shop shop = shopRepository.findById(shopId).get();
        //shop NO null
        if (shop == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pizza non trovata");
            return "redirect:/pizze";
        }
        RecordShop recordShop = new RecordShop();
        recordShop.setPizza(pizza);
        recordShop.setShop(shop);
        recordShop.setQuantitaPizzaCarrello(quantitaPizzaCarrello);
        recordShopRepository.save(recordShop);
        return "redirect:/pizze";
    }

    @GetMapping("/showShop/{shopId}")
    public String showShop(@PathVariable Long shopId, Model model, Authentication authentication) {
        boolean isAdmin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin) {
            model.addAttribute("carrelliAdmin", shopRepository.findAll());
            return "carrello/shopAdmin";
        }
        List<RecordDtoView> carrello = recordShopRepository.findCarrelloView(shopId);
        model.addAttribute("listaCarrello", carrello);
        Double sum = 0.0;
        for (RecordDtoView pizza : carrello) {
            if (pizza.getQuantitaPizzaCarrello() > pizza.getQuantitaPizza()) {
                pizza.setQuantitaPizzaCarrello(pizza.getQuantitaPizza());
            }
            sum += (pizza.getPrezzo() * pizza.getQuantitaPizzaCarrello());
        }
        model.addAttribute("prezzoCarrello", sum);
        return "carrello/shop";
    }

    @PostMapping("/deleteShop/{id}")
    public String delete(@PathVariable Long id) {
        recordShopRepository.deleteById(id);
        return "redirect:/pizze";
    }

    @PostMapping("/modificaShop/{id}")
    public String modificaShop(@PathVariable Long id,
            @RequestParam("quantitaPizzaCarrello") int nuovaQuantita,
            RedirectAttributes redirectAttributes) {
        RecordShop record = recordShopRepository.findById(id).get();
        Pizza pizza = record.getPizza(); // prendo la pizza associata al record
        if (record == null || record.getPizza() == null) {
            redirectAttributes.addFlashAttribute("errore", "Record o pizza non trovati!");
            return "redirect:/pizze";
        }

        Integer quantitaMagazzino = pizza.getQuantitaPizza();

        if (nuovaQuantita > quantitaMagazzino) {
            redirectAttributes.addFlashAttribute("errore", "Quantità: " + nuovaQuantita + " il valore inserito supera la disponibilità in magazzino!");
            return "redirect:/pizze";
        }

        redirectAttributes.addFlashAttribute("modifica", "Modifica eseguita la " + record.getPizza().getNome() + " ora ha quantità " + nuovaQuantita);
        record.setQuantitaPizzaCarrello(nuovaQuantita);
        recordShopRepository.save(record);
        return "redirect:/pizze";
    }
}
