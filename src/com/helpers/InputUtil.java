package com.helpers;

import java.util.Scanner;

public class InputUtil {
    private final Scanner scanner;

    public InputUtil(Scanner scanner) {
        this.scanner = scanner;
    }

    public String text(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    // Accepts digits only. Signs, letters and special characters are rejected.
    public int integer(String message) {
        while (true) {
            String value = text(message);
            if (value.matches("\\d+")) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid number.");
                }
            } else {
                System.out.println("Enter a valid number.");
            }
        }
    }

    // Accepts a normal non-negative decimal number.
    public double decimal(String message) {
        while (true) {
            String value = text(message);
            if (value.matches("\\d+(\\.\\d+)?")) {
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid amount.");
                }
            } else {
                System.out.println("Enter a valid amount.");
            }
        }
    }

    // Names/categories/states/cities: letters and spaces only.
    public String lettersOnly(String message, String invalidMessage) {
        while (true) {
            String value = text(message);
            if (value.matches("[A-Za-z ]+")) return value;
            System.out.println(invalidMessage);
        }
    }

    public String digitsOnly(String message) {
        while (true) {
            String value = text(message);
            if (value.matches("\\d+")) return value;
            System.out.println("Enter a valid number.");
        }
    }

    // Exactly 10 digits.
    public String mobile(String message) {
        while (true) {
            String value = text(message);
            if (value.matches("\\d{10}")) return value;
            System.out.println("Invalid mobile number.");
        }
    }

    // Email permits letters, numbers and normal email special characters.
    public String email(String message) {
        while (true) {
            String value = text(message);
            if (value.matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")) return value;
            System.out.println("Give a valid email.");
        }
    }

    // Password intentionally accepts any characters. Only an empty password is rejected.
    public String password(String message) {
        while (true) {
            String value = text(message);
            if (!value.isEmpty()) return value;
        }
    }

    public boolean yesNo(String message) {
        while (true) {
            String value = text(message).toLowerCase();
            if (value.equals("y") || value.equals("yes")) return true;
            if (value.equals("n") || value.equals("no")) return false;
            System.out.println("Please enter Y or N.");
        }
    }
}
