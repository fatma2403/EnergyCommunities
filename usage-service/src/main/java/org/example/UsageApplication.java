package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Usage Service: nimmt Nachrichten aus der Queue und aktualisiert die Datenbank.
@SpringBootApplication
public class UsageApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsageApplication.class, args);
    }
}
