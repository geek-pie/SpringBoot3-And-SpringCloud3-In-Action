package com.geekpie.jdbc.simple.simpleentity;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private final Long id;
    @Column("encrypted_password")
    private final String encryptedPassword;
}
