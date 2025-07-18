package com.pahappa.util;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

/**
 * A utility class for handling password hashing and verification using the BCrypt algorithm.
 * This is a CDI bean, so I can inject it into any service that needs it.
 */
@ApplicationScoped
public class PasswordEncoder {

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param plainTextPassword The password to hash.
     * @return A securely hashed password string.
     */
    public String encode(String plainTextPassword) {
        // BCrypt.gensalt() automatically creates a random "salt" to protect against rainbow table attacks.
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainTextPassword The password the user entered.
     * @param hashedPassword    The hash stored in the database.
     * @return true if the password matches the hash, false otherwise.
     */
    public boolean matches(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            // In case of a malformed hash, log the error and return false.
            // Consider adding a logger here.
            return false;
        }
    }
}