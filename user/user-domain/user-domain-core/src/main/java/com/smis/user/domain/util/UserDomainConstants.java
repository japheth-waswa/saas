package com.smis.user.domain.util;

import org.apache.commons.validator.routines.RegexValidator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UserDomainConstants {
    private UserDomainConstants() {
    }

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
    public static final String PASSWORD_PATTERN_ERROR_MESSAGE = "must be at least 4 characters long, contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace.";
    private static final String DIGITS = "0123456789";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = LOWERCASE.toUpperCase();
    private static final String SPECIAL_CHARACTERS = "@#$%^&+=";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean validatePassword(String password) {
        return new RegexValidator(UserDomainConstants.PASSWORD_PATTERN).isValid(password);
    }

    public static String randomPasswordGenerator(int passwordLength) {
        List<Character> passwordChars = new ArrayList<>();
        passwordChars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        passwordChars.add(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        passwordChars.add(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        passwordChars.add(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        String allChars = DIGITS + LOWERCASE + UPPERCASE + SPECIAL_CHARACTERS;
        for (int i = 4; i < passwordLength; i++) {
            passwordChars.add(allChars.charAt(RANDOM.nextInt(allChars.length())));
        }
        Collections.shuffle(passwordChars);
        StringBuilder password = new StringBuilder(passwordLength);
        for (char c : passwordChars) {
            password.append(c);
        }
        return password.toString();
    }
}
