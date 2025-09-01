package com.catia.inventory.manager;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSupervisor {
    private static UserSupervisor u = null;
    public static List<String> log_users = null;

    UserSupervisor() {
        log_users = new ArrayList<String>();
    }

    public static UserSupervisor getInstance() {
        if (u == null) {
            u = new UserSupervisor();
        }
        return u;
    }

    public static void loginUser(String name, String password, Integer app) {
        if (User.Authenticate(name, password)) {
            if (UserSupervisor.isUserLoggedIn(name)) {
                log_users.add(name);
            }
        }
    }

    public static void logoutUser(String name, String password) {

        if (UserSupervisor.isUserLoggedIn(name)) {
            log_users.remove(name);
        }
    }

    public static Boolean isUserLoggedIn(String name) {
        return log_users.contains(name);
    }
}