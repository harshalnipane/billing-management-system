package com.resort.billing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Formats Indian Currency and converts numerical amounts to words
 * in Indian numbering system (Lakhs, Crores, etc.).
 */
public class IndianCurrencyHelper {

    private static final String[] ONES = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    /**
     * Formats double to Indian Rupees string e.g. ₹ 1,25,450.00
     */
    public static String formatRupees(double amount) {
        BigDecimal bd = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        long integerPart = bd.longValue();
        int fractionPart = bd.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        String intStr = String.valueOf(Math.abs(integerPart));
        StringBuilder formatted = new StringBuilder();

        if (intStr.length() <= 3) {
            formatted.append(intStr);
        } else {
            String lastThree = intStr.substring(intStr.length() - 3);
            String rest = intStr.substring(0, intStr.length() - 3);

            StringBuilder restFormatted = new StringBuilder();
            for (int i = 0; i < rest.length(); i++) {
                if (i > 0 && (rest.length() - i) % 2 == 0) {
                    restFormatted.append(",");
                }
                restFormatted.append(rest.charAt(i));
            }
            formatted.append(restFormatted).append(",").append(lastThree);
        }

        if (integerPart < 0) {
            formatted.insert(0, "-");
        }

        return "₹ " + formatted + "." + String.format("%02d", Math.abs(fractionPart));
    }

    /**
     * Converts amount to words e.g. "Rupees Forty-Eight Thousand Five Hundred Only"
     */
    public static String convertToWords(double amount) {
        if (amount == 0) {
            return "Rupees Zero Only";
        }

        BigDecimal bd = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        long rupees = bd.longValue();
        int paise = bd.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        StringBuilder words = new StringBuilder();
        if (rupees > 0) {
            words.append("Rupees ").append(convertToIndianWords(rupees));
        }

        if (paise > 0) {
            if (rupees > 0) {
                words.append(" and ");
            }
            words.append(convertToIndianWords(paise)).append(" Paise");
        }

        words.append(" Only");
        return words.toString().replaceAll("\\s+", " ").trim();
    }

    private static String convertToIndianWords(long n) {
        if (n == 0) return "";

        StringBuilder sb = new StringBuilder();

        // Crores (10,000,000)
        if (n >= 10000000) {
            sb.append(convertToIndianWords(n / 10000000)).append(" Crore ");
            n %= 10000000;
        }

        // Lakhs (100,000)
        if (n >= 100000) {
            sb.append(convertToIndianWords(n / 100000)).append(" Lakh ");
            n %= 100000;
        }

        // Thousands (1,000)
        if (n >= 1000) {
            sb.append(convertToIndianWords(n / 1000)).append(" Thousand ");
            n %= 1000;
        }

        // Hundreds (100)
        if (n >= 100) {
            sb.append(ONES[(int) (n / 100)]).append(" Hundred ");
            n %= 100;
        }

        if (n > 0) {
            if (n < 20) {
                sb.append(ONES[(int) n]).append(" ");
            } else {
                sb.append(TENS[(int) (n / 10)]).append(" ");
                if (n % 10 > 0) {
                    sb.append(ONES[(int) (n % 10)]).append(" ");
                }
            }
        }

        return sb.toString().trim();
    }
}
