package org.example.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class Customer {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    private final String id;
    private String name;
    private String email;

    public Customer(String name, String email) {
        this.id = UUID.randomUUID().toString();
        setName(name);
        setEmail(email);
    }



    // Setters and Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.isEmpty()){
            this.name = name;
        } else {
            logger.warn("Attempted to set empty name for customer id={}", id);
        }
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        if (!email.isEmpty() && email.contains("@")) {
            this.email = email;
        } else {
            logger.warn("Attempted to set invalid email for customer id={}, email={}", id, email);
        }
    }
}
