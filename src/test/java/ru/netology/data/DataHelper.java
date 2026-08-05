package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;

public class DataHelper {
    private static final Faker faker = new Faker(new Locale("ru"));

    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationCode {
        String code;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static VerificationCode getVerificationCode() {
        return new VerificationCode("12345");
    }

    public static String getFirstCardNumber() {
        return "5559 0000 0000 0001";
    }

    public static String getSecondCardNumber() {
        return "5559 0000 0000 0002";
    }

    public static int getInitialCardBalance() {
        return 10000;
    }

    public static int generateValidTransferAmount(int currentBalance) {
        return faker.number().numberBetween(1, currentBalance);
    }

    public static String getLastFourDigits(String cardNumber) {
        String digits = cardNumber.replaceAll("\\s", "");
        return digits.substring(digits.length() - 4);
    }
}