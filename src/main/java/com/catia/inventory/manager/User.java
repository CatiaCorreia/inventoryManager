package com.catia.inventory.manager;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;
import java.nio.charset.StandardCharsets;

public class User {

    @Autowired private static DataSource dataSource;

    private long id;
    private String username;
    private String password;

    private static final Random RANDOM = new SecureRandom();

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

    public void addUser(String username, String password) {
        String salt = getSalt();
        String encrypted_pass = encryptPass(password, salt);

        String sql = "INSERT INTO users (user_name, hash, salt) VALUES (?, ?, ?)";


        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);

            pstm.setString(1, username);
            pstm.setString(2, encrypted_pass);
            pstm.setString(3, salt);
            pstm.executeUpdate();

            System.out.println("User added to database: " + username);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteUser(String username, String url) {

        if (UserSupervisor.isUserLoggedIn(username)) {
            String sql = "DELETE FROM users WHERE user_name = ?";

            try (Connection conn = dataSource.getConnection()) {
                PreparedStatement pstm = conn.prepareStatement(sql);

                pstm.setString(1, username);
                pstm.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String encryptPass(String pass, String salt) {
        String p_salted = pass + salt;
        String encripted_pass = hash(p_salted);
        return encripted_pass;
    }

    public String getSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String s_salt = Base64.getEncoder().encodeToString(salt);
        return s_salt;
    }

    public static String hash(String pass) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        byte[] passwordBytes = pass.getBytes(StandardCharsets.UTF_8);
        byte[] hash = new byte[32];

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder();
        builder.withIterations(3).withMemoryAsKB(16 * 1024).withParallelism(4);
        Argon2Parameters parameters = builder.build();

        generator.init(parameters);
        generator.generateBytes(passwordBytes, hash);

        String s_hash = Base64.getEncoder().encodeToString(hash);
        return s_hash;
    }


    public static Boolean Authenticate(String name, String password) {
        String query = "SELECT password, salt FROM users WHERE user_name = ?";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, name);

            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                String hash = res.getString("password");
                String salt = res.getString("salt");

                System.out.println("Found user: " + name);
                System.out.println("Hash: " + hash);
                System.out.println("Salt: " + salt);

                String hashed_pass = encryptPass(password, salt);

                System.out.println("Computed hash: " + hashed_pass);
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
