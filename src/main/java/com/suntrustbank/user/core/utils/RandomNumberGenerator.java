package com.suntrustbank.user.core.utils;

import java.security.SecureRandom;

public class RandomNumberGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generates a random numeric code with the specified number of digits.
     *
     * @param numberOfDigits The number of digits for the code.
     * @return A random numeric code as a String.
     * @throws IllegalArgumentException if the number of digits is less than 1.
     */
    public static String generate(int numberOfDigits) {
        if (numberOfDigits < 1) {
            throw new IllegalArgumentException("Number of digits must be at least 1");
        }

        // Create a range for the code based on the number of digits
        int lowerBound = (int) Math.pow(10, numberOfDigits - 1);
        int upperBound = (int) Math.pow(10, numberOfDigits) - 1;

        // Generate a random number within the range
        int randomNumber = RANDOM.nextInt(upperBound - lowerBound + 1) + lowerBound;

        return String.valueOf(randomNumber);
    }

    /**
     * Generates a random alphanumeric string with the specified length.
     *
     * @param length The length of the alphanumeric string.
     * @return A random alphanumeric string.
     * @throws IllegalArgumentException if the length is less than 1.
     */
    public static String generateAlphanumericCode(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be at least 1");
        }

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length());
            result.append(ALPHANUMERIC_CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}
