package com.pahappa.util;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import java.util.Scanner;


public class StringUtil {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * @exception  NumberFormatException occurs when an attempt is made to
     * convert a string with improper format into a numeric value
     */

    public static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static Date getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (YYYY-MM-DD): ");
                String input = scanner.nextLine();
                String[] parts = input.split("-");
                if (parts.length == 3) {
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[0]);
                    if (month < 1 || month > 12) {
                        System.out.println("Invalid month. Please enter a month between 01 and 12.");
                        continue;
                    }
                if (day < 0 || day > 31) {
                        System.out.println("Invalid day. Please enter a day between 01 and 31.");
                        continue;
                    }
                    return dateFormat.parse(input);
                } else {
                    System.out.println("Please enter a valid date in the format YYYY-MM-DD.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid month. Please enter a valid number for the month.");
            } catch (ParseException e) {
                System.out.println("Please enter a valid date in the format YYYY-MM-DD.");
            }
        }
    }


}
