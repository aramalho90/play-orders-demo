package models;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.List;

@Entity
public class Stock extends Model {

    @Id
    @Constraints.Required
    @Constraints.Min(1)
    public Integer stockId;
    @Constraints.Required
    public String creationDate;
    @Constraints.Required
    public Integer itemId;
    @Constraints.Required
    public Integer quantity;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    public Item item;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Orders> orders;

    public static Finder<Integer,Stock> find = new Finder<>(Stock.class);
}