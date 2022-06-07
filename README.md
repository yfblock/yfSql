# yfSql
> 一个独立的JDBC框架 
> Author: Yufeng

### 一、安装

##### 使用maven方式安装

```xml
<dependency>
  <groupId>io.github.yfblock</groupId>
  <artifactId>yfSql</artifactId>
  <version>1.0.11</version>
</dependency>
```

##### 使用gradle方式安装

```groovy
implementation 'io.github.yfblock:yfSql:1.0.11'
annotationProcessor 'io.github.yfblock:yfSql:1.0.11'

```

### 二、添加数据库

​	此数据库操作库仅仅提供操作接口  需要再添加数据库依赖，内置支持mysql和sqlite

### 三、设置数据库

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

###### 异常抛出
> 默认情况下异常将由框架进行出来
> 
> 如果需要进行异常抛出 可在wrapper中加入throws 目前只支持SQLException
> 
> 如下

```java
import java.sql.SQLException;

@DataRunner(runner = SqliteRunner.class, path = "test.db")
public class UserWrapper {

    @Select("select * from user")
    public ArrayList<User> getUsers() throws SQLException {
        return null;
    }
}
```

##### 五、链式操作方式

> 暂时取消链式操作

### 六、开发计划

- 计划支持字段校验功能，执行前检测参数是否符合正则
- 支持多数据库，多表联合查询(暂未有很好的思路，有兴趣可以在issus中提出)