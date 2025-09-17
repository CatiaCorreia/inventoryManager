package com.catia.inventory.manager.model;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS")
public class User {

    @Id
    private Integer id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String hash;

    @NotEmpty
    private String salt;

    public User(String username, String hash, String salt){
        this.username = username;
        this.hash = hash;
        this.salt = salt;
    }


    public String getUsername(){
        return this.username;
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return ("username: " + username + "\nhash: " + hash + "\nsalt: " + salt);

    }
}
