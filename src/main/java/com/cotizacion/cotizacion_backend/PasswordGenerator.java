package com.cotizacion.cotizacion_backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "123456";

        String hash = encoder.encode(password);

        System.out.println("=================================");
        System.out.println("Contraseña: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("=================================");
    }
}
