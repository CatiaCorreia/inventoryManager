package com.catia.inventory.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired final UserSupervisor usuper;

    public UserController(UserSupervisor usuper) {
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

    @GetMapping("/login")
    public String login(@RequestBody User user){
        return usuper.loginUser(user.getUsername(), user.getPass()).toString();
    }

    @GetMapping("/logout/{name}")
    @ResponseBody
    public String logout(@PathVariable String name){
        return usuper.logoutUser(name).toString();
    }

    @PostMapping("/delete/{name}")
    @ResponseBody
    public String deleteUser(@PathVariable String name){
        return usuper.deleteUser(name);
    }
}