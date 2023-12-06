package com.geekpie.jdbc.simple;

import com.geekpie.jdbc.simple.simpleentity.User;
import com.geekpie.jdbc.simple.simpleentity.UserConfiguration;
import com.geekpie.jdbc.simple.simpleentity.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.util.Assert;

/**
 * @author xujianxing
 */

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
