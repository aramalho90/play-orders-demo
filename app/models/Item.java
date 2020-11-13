package models;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Item extends Model {
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @Constraints.Required
    @Constraints.Min(1)
    public Integer itemId;
    @Constraints.Required
    public String name;

    @OneToMany(mappedBy = "item")
    public List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    public List<Stock> stocks = new ArrayList<>();

    public static Finder<Integer,Item> find = new Finder<>(Item.class);
}