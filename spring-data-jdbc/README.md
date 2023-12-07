
Spring data jdbc是基于DDD的
分为两个部分，基于传统的MVC的方式以及DDD的，基于MVC的主要是对基础的使用。


https://docs.pingcap.com/zh/tidb/stable/quick-start-with-tidb#%E9%83%A8%E7%BD%B2%E6%9C%AC%E5%9C%B0%E6%B5%8B%E8%AF%95%E9%9B%86%E7%BE%A4


https://github.com/spring-projects/spring-data-example


```
spring:
  datasource:
    url: jdbc:mysql://localhost:4000/test
    username: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```
报错。
原因：
https://stackoverflow.com/questions/49088847/after-spring-boot-2-0-migration-jdbcurl-is-required-with-driverclassname

```sql
CREATE TABLE IF NOT EXISTS `users`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `encrypted_password` VARCHAR(10)  NOT NULL COMMENT '加密后的密码'
) ENGINE = INNODB
  charset = utf8mb4 COMMENT '用户信息表';
```


```java
import com.geekpie.jdbc.simple.simpleentity.User;
import com.geekpie.jdbc.simple.simpleentity.UserConfiguration;
import com.geekpie.jdbc.simple.simpleentity.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xujianxing
 */
@SpringBootTest(classes = UserConfiguration.class)
@AutoConfigureJdbc
public class SimpleEntityTests {


    @Autowired
    private UserRepository userRepository;

    @Test
    void directInsert() {
        User user = new User(System.currentTimeMillis(), "password");
        userRepository.save(user);
    }
}
```

如果直接save,会发现报错：



```java
Caused by: org.springframework.dao.IncorrectUpdateSemanticsDataAccessException: Failed to update entity [User(id=1701833003484, encryptedPassword=password)]; Id [1701833003484] not found in database
	at org.springframework.data.jdbc.core.JdbcAggregateChangeExecutionContext.updateWithoutVersion(JdbcAggregateChangeExecutionContext.java:339)
	at org.springframework.data.jdbc.core.JdbcAggregateChangeExecutionContext.executeUpdateRoot(JdbcAggregateChangeExecutionContext.java:129)
	at org.springframework.data.jdbc.core.AggregateChangeExecutor.execute(AggregateChangeExecutor.java:93)
	... 34 more
	```
	
	这个是因为save方法其实是update。而非insert。
	
	
	因此，如果要insert，应该使用如下的方法：
	
	```java
@SpringBootTest(classes = UserConfiguration.class)
@AutoConfigureJdbc
public class SimpleEntityTests {
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void directInsert() {
        User user = new User(System.currentTimeMillis(), "password");
        jdbcAggregateTemplate.insert(user);
        userRepository.save(user);
    }
}
```


很显然，我们可以看到，ID是AUTO_INCREMENT的，并且其实无论是id和password 我们都希望是不能发生变化的。这有两种形式：
1.final的形式
2.用record模型。

## @PersistenceCreator注解
https://stackoverflow.com/questions/74360071/what-is-the-use-of-persistencecreator-annotation

```java
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__(@PersistenceCreator))
```

表示将构造方法进行私有，通过PersistenceCreator生成的静态方法来构建。


```
@Data
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private final Long id;
    @Column("encrypted_password")
    private final String encryptedPassword;
}

@SpringBootTest(classes = UserConfiguration.class)
@AutoConfigureJdbc
public class SimpleEntityTests {
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void directInsert() {
        Long id = System.currentTimeMillis();
        User user = new User(id, "password");
        jdbcAggregateTemplate.insert(user);
        user = userRepository.findById(id).get();
        Assert.notNull(user, "user is null");
        System.out.println(user.getEncryptedPassword());
    }
}
```

可以看到成功的输出，并且final对象只有get方法。
https://github.com/spring-projects/spring-data-examples/blob/main/jdbc/basics/src/main/java/example/springdata/jdbc/basics/simpleentity/Category.java



另外一个问题：
在很多情况下，ID是不需要外部传进来的。该怎么处理?

参考Category
先with，然后再创建

返回了一个全新的对象。