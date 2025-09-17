package com.catia.inventory.manager.service;

import com.catia.inventory.manager.UserRepository;
import com.catia.inventory.manager.model.User;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Scope("singleton")
@Service
public class UserSupervisor {

    private final Random RANDOM = new SecureRandom();
    public List<String> log_users = null;
    final private  DataSource dataSource;
    final private UserRepository userRepository;


    UserSupervisor(DataSource dataSource, UserRepository userRepository) {
        log_users = new ArrayList<String>();
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    public Boolean loginUser(Integer id, String name, String password) {
        if (authenticate(id, name, password)) {
            if(!isUserLoggedIn(name)){
                log_users.add(name);
                System.out.println(("User " + name + " logged in."));
            }
            else System.out.println("User already logged in.");
        }
        else System.out.println("Wrong credentials");
        return isUserLoggedIn(name);
    }

    public Boolean logoutUser(Integer id) {
        User u = userRepository.findById(id).orElse(null);

        if(u != null) {
            String name = u.getUsername();
            if (isUserLoggedIn(name)) {
                log_users.remove(name);
                System.out.println(name + " logged out.");
            }
            return !(isUserLoggedIn(name));
        }
        return false;
    }

    public Boolean isUserLoggedIn(String name) {
        return log_users.contains(name);
    }

    public String addUser(String username, String password) {
        if(password == null || password.isEmpty()) return "No user added - invalid password";

        if(uniqueUsername(username)){
            String salt = getSalt();
            String encrypted_pass = encryptPass(password, salt);

            User u = new User(username, encrypted_pass, salt);

            userRepository.save(u);
            return "User successfully added.";
        }
        else return "That username already exists, please choose another";
    }

    private boolean uniqueUsername(String username) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE username = ?";
        try (Connection conn = dataSource.getConnection()){
            PreparedStatement pstm = conn.prepareStatement(sql);

            pstm.setString(1,username);
            ResultSet result = pstm.executeQuery();
            if (result.next()){
                return result.getInt("COUNT(*)") == 0;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public String deleteUser(Integer id) {

        User u = userRepository.findById(id).orElse(null);
        if(u != null){
            String username = u.getUsername();
            if (isUserLoggedIn(username)) {
                userRepository.deleteById(id);
                logoutUser(id);
                return ("User " + username + " deleted.");

            }
        }
        return ("Error - User not logged in.");
    }

    public String encryptPass(String pass, String salt) {
        String p_salted = pass + salt;
        return hash(p_salted);
    }

    public String getSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hash(String pass) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        byte[] passwordBytes = pass.getBytes(StandardCharsets.UTF_8);
        byte[] hash = new byte[32];

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder();
        builder.withIterations(3).withMemoryAsKB(16 * 1024).withParallelism(4);
        Argon2Parameters parameters = builder.build();

        generator.init(parameters);
        generator.generateBytes(passwordBytes, hash);

        return Base64.getEncoder().encodeToString(hash);
    }


    public Boolean authenticate(Integer id, String name, String password) {
        User u = userRepository.findById(id).orElse(null);

        if (u == null) {
            System.out.println("User not found: " + name);
            return false;
        }
        else {
            String hashed_pass = encryptPass(password, u.getSalt());
            return hashed_pass.equals(u.getHash());
        }
    }
}