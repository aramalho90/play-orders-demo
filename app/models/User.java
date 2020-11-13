package models;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends Model {

    @Id
    @Constraints.Required
    @Constraints.Min(1)
    public Integer userId;
    @Constraints.Required
    public String name;
    @Constraints.Required
    public String email;

    @OneToMany(mappedBy = "user")
    public List<Orders> orders = new ArrayList<>();

    public static Finder<Integer,User> find = new Finder<>(User.class);
}