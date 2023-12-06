package com.geekpie.jdbc.simple;

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
//    @Autowired
//    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void directInsert() {
        User user = new User(System.currentTimeMillis(), "password");
//        jdbcAggregateTemplate.insert(user);
        userRepository.save(user);
    }
}
