package com.resort.billing.util;

import java.util.regex.Pattern;

/**
 * Validations for Indian GSTIN, PAN, Phone numbers, and Email.
 */
public class ValidationHelper {

    // Indian GSTIN format: 2 digits (State Code) + 5 letters (PAN) + 4 digits (PAN) + 1 letter (PAN) + 1 digit/char + 1 letter (Z) + 1 check digit
    private static final Pattern GSTIN_PATTERN = 
        Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$");

    private static final Pattern PAN_PATTERN = 
        Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");

    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[6-9]\\d{9}$");

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isValidGstin(String gstin) {
        if (gstin == null || gstin.trim().isEmpty()) return true; // Optional for B2C
        return GSTIN_PATTERN.matcher(gstin.trim().toUpperCase()).matches();
    }

    public static boolean isValidPan(String pan) {
        if (pan == null || pan.trim().isEmpty()) return false;
        return PAN_PATTERN.matcher(pan.trim().toUpperCase()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.length() == 12 && cleaned.startsWith("91")) {
            cleaned = cleaned.substring(2);
        }
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // Optional
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static String extractStateCodeFromGstin(String gstin) {
        if (gstin != null && gstin.trim().length() >= 2) {
            return gstin.trim().substring(0, 2);
        }
        return "";
    }
}
