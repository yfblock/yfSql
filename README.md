# yfSql
> 一个独立的JDBC框架 
> Author: Yufeng

### 一、安装

##### 使用maven方式安装

```xml
<dependency>
  <groupId>io.github.yfblock</groupId>
  <artifactId>yfSql</artifactId>
  <version>1.0.8</version>
</dependency>
```

##### 使用gradle方式安装

```groovy
implementation 'io.github.yfblock:yfSql:1.0.8'
annotationProcessor 'io.github.yfblock:yfSql:1.0.8'

```

### 二、添加数据库

​	此数据库操作库仅仅提供操作接口  需要再添加数据库依赖，内置支持mysql和sqlite

### 三、设置数据库

##### xml方式

首先需要在数据库操作Wrapper类中设置注解

```java
@DataRunner(useProperty = true)
public class UserWrapper {
	
}
```

然后再application.properties中添加

```properties
yf.mysql.host=localhost					// 数据库连接地址
yf.mysql.port=3306						// 端口
yf.mysql.username=root					// 用户名
yf.mysql.password=root					// 密码
yf.mysql.database=java-orm-test			// 数据库名称
yfSql.mysql.driver=com.mysql.cj.jdbc.Driver		// jdbc驱动，为支持5和8 缺省为com.mysql.cj.jdbc.Driver 可选
```

##### 注解方式

```java
// mysql
// 默认情况下 hostname为localhost port为3306 username为root password为root 若系统配置相同 则可以使用默认设置
@DataRunner(database = "java-orm-test", hostname = "localhost", port = "3306", username = "root", password = "root")
public class UserWrapper {
}

//sqlite
@DataRunner(runner = SqliteRunner.class, path = "test.db")
public class UserWrapper {
}
```

使用时设置`runner`，如果使用其他数据库则可以实现`SqlRunner`接口来定制支持其他数据库

##### 四、使用wrapper

实体类User.java eg: 使用时尽量使用引用类型

```java
package Test;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String qq;
    private String phone;
}

```

UserWrapper.java

```java
@DataRunner(runner = SqliteRunner.class, path = "test.db")
public class UserWrapper {

    @Select("select * from user")
    public ArrayList<User> getUsers() {return null;}

    @Select("select * from user where username={0} and password={1}")
    public User login(String username, String password) {return null;}

    @Insert("insert into user (username, password) VALUES ({0}, {1})")
    public Integer register(String username, String password) { return 0;}

    @Update("update user set qq={0} where id={1}")
    public void updateQQById(String qq, int id){}

    @Delete("delete from user where id={0}")
    public void deleteUserById(int id){}

    @Select("select * from user where balance > {0}")
    public User getUserByBalance(int balance) {return null;}
}

```

上述为增删改查结构样例，使用注解方式定义Sql，编译时根据Sql生成函数主体代码，函数主体可缺省，但是推荐添加return null;等空语句，便于编辑器查找语法错误。

调用

```java
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
```

##### 五、链式操作方式

> 链式操作时仅可同时操作单个数据表

CartView.java

```java
@Data
public class CartView {
    private Integer id;                 // 购物车记录编号
    private Integer goodId;             // 商品编号
    private Integer userId;             // 用户编号
    private Integer number;             // 购买数量
    private String cover;               // 商品封面
    private String name;                // 商品名称
    private Double price;               // 商品单价
}

```

调用

```java
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
```

### 六、开发计划

- 计划支持字段校验功能，执行前检测参数是否符合正则
- 支持多数据库，多表联合查询(暂未有很好的思路，有兴趣可以在issus中提出)