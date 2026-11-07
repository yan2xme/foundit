package com.mycompany.foundit;

import java.util.Scanner;

/**
 * Reads and parses console input for the menus.
 */
public final class Console {

    private final Scanner in;

    public Console(Scanner in) {
        this.in = in;
    }

    public String readLine(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    public int readInt(String label) {
        return Integer.parseInt(readLine(label));
    }

    /**
     * Returns -1 for non-numeric input so callers fall through to their "invalid choice" branch.
     */
    public int readChoice(String label) {
        try {
            return Integer.parseInt(readLine(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void printMenu(String title, String... options) {
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }
}
