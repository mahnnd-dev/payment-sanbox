package com.neo.util;

import java.security.SecureRandom;

public class TmnCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateTmnCode() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("Fake TmnCode: " + generateTmnCode());
    }
}