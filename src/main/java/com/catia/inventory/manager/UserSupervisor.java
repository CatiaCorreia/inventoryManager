package com.catia.inventory.manager;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
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
    @Autowired private  DataSource dataSource;


    UserSupervisor(DataSource dataSource) {
        log_users = new ArrayList<String>();
        this.dataSource = dataSource;
    }

    public Boolean loginUser(String name, String password) {
        if (authenticate(name, password)) {
            if(!isUserLoggedIn(name)){
                log_users.add(name);
            }
            else System.out.println("User already logged in.");
        }
        else System.out.println("Wrong credentials");
        return isUserLoggedIn(name);
    }

    public Boolean logoutUser(String name) {
        if (isUserLoggedIn(name)) {
            log_users.remove(name);
            System.out.println(name + " logged out.");
        }
        return !(isUserLoggedIn(name));
    }

    public Boolean isUserLoggedIn(String name) {
        return log_users.contains(name);
    }

    public String addUser(String username, String password) {
        if(password == null || password.isEmpty()) return "No user added - invalid password";

        if(uniqueUsername(username)){
            String salt = getSalt();
            String encrypted_pass = encryptPass(password, salt);

            String sql = "INSERT INTO users (user_name, hash, salt) VALUES (?, ?, ?)";


            try (Connection conn = dataSource.getConnection()) {
                PreparedStatement pstm = conn.prepareStatement(sql);

                pstm.setString(1, username);
                pstm.setString(2, encrypted_pass);
                pstm.setString(3, salt);
                pstm.executeUpdate();

                return ("User added to database: " + username);
            } catch (SQLException e) {
                return (e.getMessage());
            }
        }
        else return "That username already exists, please choose another";
    }

    private boolean uniqueUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
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

    public String deleteUser(String username) {

        if (isUserLoggedIn(username)) {
            String sql = "DELETE FROM users WHERE user_name = ?";

            try (Connection conn = dataSource.getConnection()) {
                PreparedStatement pstm = conn.prepareStatement(sql);

                pstm.setString(1, username);
                pstm.executeUpdate();
                logoutUser(username);
                return ("User " + username + " deleted.");

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return ("Error - User not deleted.");
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


    public Boolean authenticate(String name, String password) {
        String query = "SELECT hash, salt FROM users WHERE user_name = ?";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, name);

            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                String hash = res.getString("hash");
                String salt = res.getString("salt");

                System.out.println("Found user: " + name);

                String hashed_pass = encryptPass(password, salt);
                return hashed_pass.equals(hash);

            } else {
                System.out.println("User not found: " + name);
            }

        } catch (SQLException e) {
            System.out.println("Error during authentication: " + e.getMessage());
        }
        return false;
    }
}