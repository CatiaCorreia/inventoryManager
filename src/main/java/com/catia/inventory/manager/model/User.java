package com.catia.inventory.manager.model;

import jakarta.validation.constraints.NotEmpty;

public class User {

    private long id;

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;


    public User(long id, String username, String hash, String salt){
        this.id = id;
        this.username = username;
    }

    public long getId(){
        return this.id;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPass(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

}
