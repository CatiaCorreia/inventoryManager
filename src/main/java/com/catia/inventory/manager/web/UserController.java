package com.catia.inventory.manager.web;

import com.catia.inventory.manager.service.UserSupervisor;
import com.catia.inventory.manager.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired final UserSupervisor usuper;

    public UserController(UserSupervisor usuper) {
        this.usuper = usuper;
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody Map<String, String> userData){
        return usuper.addUser(userData.get("username"), userData.get("password"));
    }

    @GetMapping("/login")
    public String login(@RequestBody Map<String, String> userData){
        return usuper.loginUser(Integer.parseInt(userData.get("id")), userData.get("username"), userData.get("password")).toString();
    }

    @GetMapping("/logout/{id}")
    @ResponseBody
    public String logout(@PathVariable Integer id){
        return usuper.logoutUser(id).toString();
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Integer id){
        return usuper.deleteUser(id);
    }
}