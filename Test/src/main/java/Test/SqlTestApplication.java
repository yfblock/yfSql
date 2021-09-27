package Test;

import io.github.yfblock.yfSql.Runner.MysqlRunner;
import io.github.yfblock.yfSql.Sql.DataTableWrapper;

import java.util.ArrayList;

public class SqlTestApplication {
    public static void main(String[] args) {
        MysqlRunner mysqlRunner = new MysqlRunner("root", "root", "java-orm-test");
        DataTableWrapper<CartView> cartViewDataTable = new DataTableWrapper<>(CartView.class, mysqlRunner);

        // * select example
        ArrayList<CartView> cartViews = cartViewDataTable.select();
        for(CartView cartView : cartViews) {
            System.out.println(cartView.getName());
        }

        // * add example
        CartView cartView = new CartView();
        cartView.setName("Hello");
        cartView.setNumber(12);
        cartView.setPrice(30.0);
        cartViewDataTable.add(cartView);

        // * find and update example
        cartView = cartViewDataTable.where("name", "Hello").find();
        cartView.setPrice(20.0);
        cartViewDataTable.where("name", "Hello").update(cartView);

        // * count example
        int count = cartViewDataTable.count();
        System.out.println("Count: " + count);

        // * delete example
        cartViewDataTable.where("name", "Hello").delete();

        // * custom select example
        cartViews =
                DataTableWrapper.executeQuery("select * from cart_view", CartView.class, mysqlRunner);

//        CartViewGroup cartViewGroup = new CartViewGroup();
//        cartViewGroup.getAll();
//        CartViewGroup cartViewGroup = new CartViewGroup();
//        for(CartView cartView : cartViewGroup.getAll()) {
//            System.out.println(cartView.getName());
//        }
//        System.out.println(cartViewGroup.getCartViewById(2));
//        DataTableWrapper<Test> testDataTableWrapper = new DataTableWrapper<>(Test.class, mysqlRunner);
//        for(Test test : testDataTableWrapper.where("id < 3").select()) {
//            System.out.println(test.getTest1());
//        }

//        TestWrapper testWrapper = new TestWrapper();
//        for(Test test : testWrapper.getTestIdLess(3)) {
//            System.out.println(test.getTest1());
//        }
//        Test test = testWrapper.login("1", "2");
//        if(test != null) {
//            System.out.println(test.getId());
//            System.out.println(test.getTest2());
//        }
//        else System.out.println("未找到用户!");

        UserWrapper userWrapper = new UserWrapper();
        System.out.println(userWrapper.getUserByBalance(1200));
        // 增
        userWrapper.register("admins", "123");
        // 删
        userWrapper.deleteUserById(25);
        // 改
        userWrapper.updateQQById("321", 10);
        // 查
        User user = userWrapper.login("admin", "21232f297a57a5a743894a0e4a801fc3");
        if(user!=null) System.out.println(user.getId());

        ArrayList<User> users = userWrapper.getUsers();
    }
}
