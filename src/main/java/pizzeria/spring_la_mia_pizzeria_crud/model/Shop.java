package pizzeria.spring_la_mia_pizzeria_crud.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Inserire un valore corretto")
    @DateTimeFormat(pattern = "yyy-MM-dd")
    private LocalDate dataDiCreazione;

    @OneToMany(mappedBy = "shop")
    private List<RecordShop> recordShop;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany(mappedBy = "shop")//è il nome della lista
    @JsonBackReference
    private List<Pizza> pizza;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RecordShop> getRecordShop() {
        return recordShop;
    }

    public void setRecordShop(List<RecordShop> recordShop) {
        this.recordShop = recordShop;
    }

    public LocalDate getDataDiCreazione() {
        return dataDiCreazione;
    }

    public void setDataDiCreazione(LocalDate dataDiCreazione) {
        this.dataDiCreazione = dataDiCreazione;
    }

    public List<Pizza> getPizza() {
        return pizza;
    }

    public void setPizza(List<Pizza> pizza) {
        this.pizza = pizza;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
