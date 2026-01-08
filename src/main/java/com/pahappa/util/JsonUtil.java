package com.pahappa.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A utility class to handle JSON serialization using Google's Gson library.
 */
public class JsonUtil {

    // Create a single, reusable Gson instance with pretty printing enabled.
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting() // Makes the JSON output human-readable with indentation.
            .serializeNulls()    // Ensures that fields with null values are included in the output.
            .create();

    /**
     * Converts any Java object into a nicely formatted JSON string.
     * This replaces the need to use the basic .toString() method for audit logging.
     *
     * @param object The object to serialize.
     * @return A JSON string representation of the object, or null if the input is null.
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            // This is where the magic happens! Gson converts the object to JSON.
            return gson.toJson(object);
        } catch (Exception e) {
            // As a fallback, if serialization fails for any reason,
            // log the error and return the basic toString() representation.
            // This ensures that the audit logging process itself doesn't crash.
            e.printStackTrace();
            return object.toString();
        }
    }
}