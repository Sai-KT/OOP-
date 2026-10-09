package com.college.sms.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ==============================================================================
 * ContactDetails (OOP Concept: Composition & Encapsulation)
 * ==============================================================================
 * Represents contact information owned by a Student.
 * Demonstrates:
 * 1. Composition: Life cycle is bound to the owning Student entity.
 * 2. Encapsulation: Internal contact fields are private with strict validation.
 * 3. Information Hiding: Controlled access through validated getters and setters.
 * ==============================================================================
 */
public class ContactDetails {

    // Regular expression for validating academic / standard email format
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private String email;
    private String phoneNumber;

    /**
     * Default constructor for serialization / dynamic initialization.
     */
    public ContactDetails() {
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Parameterized Constructor (Encapsulation with validation).
     *
     * @param email       Valid email address
     * @param phoneNumber Contact phone digits
     */
    public ContactDetails(String email, String phoneNumber) {
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty.");
        }
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + trimmed);
        }
        this.email = trimmed;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            this.phoneNumber = "";
        } else {
            this.phoneNumber = phoneNumber.trim();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactDetails that = (ContactDetails) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }

    @Override
    public String toString() {
        return "ContactDetails{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
