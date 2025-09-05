package com.catia.inventory.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld{

    @Autowired final UserSupervisor usuper;

    public HelloWorld(UserSupervisor usuper) {
        this.usuper = usuper;
    }

    @GetMapping("/")
    public String Hello(){
        return "Hello World";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody User user){
        return usuper.addUser(user.getUsername(), user.getPass());
    }
}