# yfSql
> 一个独立的JDBC框架 
> Author: Yufeng

### 一、安装

##### 使用maven方式安装

```xml
<dependency>
  <groupId>io.github.yfblock</groupId>
  <artifactId>yfSql</artifactId>
  <version>1.1.3</version>
</dependency>
```

##### 使用gradle方式安装

```groovy
implementation 'io.github.yfblock:yfSql:1.1.3'
annotationProcessor 'io.github.yfblock:yfSql:1.1.3'
```

### 二、添加数据库

此数据库操作库仅仅提供操作接口  需要再添加数据库依赖

### 三、设置数据库

需要添加 `DataSource`, `yfSql` 内默认内置一个简单的 `BasicDataSource` 可供简单的项目使用.

如果需要使用其他的数据库，配置数据库时，需要在`构造函数`调用 `super("jdbc_driver', 'url', 'username', 'password')`

##### 四、使用wrapper

实体类User.java eg: 使用时尽量使用引用类型

```java
package Test;

import lombok.Data;
import io.github.yfblock.yfSql.Annotation.DataField;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String qq;
    // 如果java中的属性名与数据库中不对应， 则可以使用DataField注解
    @DataField("phone")
    private String userPhone;
}

```

UserWrapper.java

```java
@DataRunner(DatabaseConfig.class)
// 目前只能在 interface 上使用
public interface UserWrapper {

    @Select("select * from user")
    public ArrayList<User> getUsers() throws SQLException;

    // Find 时返回值不需要 List
    @Find("select * from user where username={0} and password={1}")
    public User login(String username, String password) throws SQLException;

    @Find("select * from user where balance > {0}")
    public User getUserByBalance(int balance) throws SQLException;
    
    @Insert("insert into user (username, password) VALUES ({0}, {1})")
    public Integer register(String username, String password) throws SQLException;

    @Update("update user set qq={0} where id={1}")
    public void updateQQById(String qq, int id) throws SQLException;

    @Delete("delete from user where id={0}")
    public void deleteUserById(int id) throws SQLException;
}

```

上述为增删改查结构样例，使用注解方式定义Sql，编译时根据Sql生成函数主体代码

调用

```java
public class WrapperTest {
    public static void Main(String args[]) {
        // 也可使用 druid 来调用
        DataSource dataSource = new BasicDataSource("org.sqlite.JDBC", "jdbc:sqlite:test.db", "", "");
        // mysql
        // DataSource dataSource = new BasicDataSource("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/数据库名称", "", "");
        UserWrapper userWrapper = TestServiceInterface test = DataRunnerUtil.getWrapper(UserWrapper.class, dataSource);
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
```

###### 异常抛出

需要在调用时对 `SQLException` 异常进行处理

### 五、springboot 集成

本项目可与 `springboot`, `Druid` 集成具体请参照本项目内 `springboot`, `springboot-druid` 这两个 `example`.

### 六、开发计划

- 单列查询时，直接返回对应的 `List`
- 如果函数不需要处理 SQLException， 直接由程序进行生成相应的 `try` `catch` 结构。