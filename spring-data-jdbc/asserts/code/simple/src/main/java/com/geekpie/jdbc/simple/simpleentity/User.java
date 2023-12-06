package com.geekpie.jdbc.simple.simpleentity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    private @Id
    Long id;

    @Column("encrypted_password")
    private String encryptedPassword;

}
