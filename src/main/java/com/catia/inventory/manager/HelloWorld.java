package com.catia.inventory.manager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld{

    private final UserSupervisor usuper = new UserSupervisor();

    @GetMapping("/")
    public String Hello(){
        return "Hello World";
    }

    @GetMapping("/addUser")
    public String adduser(){
        return "Hello World";
    }
}