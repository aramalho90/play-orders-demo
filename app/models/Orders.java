package models;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.List;


@Entity
public class Orders extends Model {

    @Id
    @Constraints.Required
    @Constraints.Min(1)
    public Integer orderId;
    @Constraints.Required
    public String creationDate;
    @Constraints.Required
    @Constraints.Min(1)
    public Integer itemId;
    @Constraints.Required
    @Constraints.Min(1)
    public Integer quantity;
    @Constraints.Required
    @Constraints.Min(1)
    public Integer userId;
    public Integer fulfilled;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    public User user;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    public Item item;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "order_stocks")
    public List<Stock> stocks;

    public static Finder<Integer,Orders> find = new Finder<>(Orders.class);
}
