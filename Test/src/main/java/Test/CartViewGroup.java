package Test;

import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;

import java.util.ArrayList;

@DataRunner(username = "root", password = "root", database = "java-orm-test")
public class CartViewGroup {
    @Select("select * from cart_view")
    public ArrayList<CartView> getAll() { return null; }

    @Select("select * from cart_view where id={0}")
    public CartView getCartViewById(int id) { return null; }
}
