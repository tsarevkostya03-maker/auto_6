package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {
    private DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 15000;
        Configuration.headless = false;

        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        System.out.println("=== Initial balances ===");
        System.out.println("First card balance: " + initialFirstCardBalance);
        System.out.println("Second card balance: " + initialSecondCardBalance);

        int transferAmount = DataHelper.generateValidTransferAmount(initialFirstCardBalance);
        System.out.println("Transfer amount: " + transferAmount);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        DashboardPage newDashboardPage = transferPage.makeTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        System.out.println("=== After transfer ===");
        System.out.println("Actual first card balance: " + actualFirstCardBalance);
        System.out.println("Actual second card balance: " + actualSecondCardBalance);

        int expectedFirstCardBalance = initialFirstCardBalance - transferAmount;
        int expectedSecondCardBalance = initialSecondCardBalance + transferAmount;

        System.out.println("Expected first card balance: " + expectedFirstCardBalance);
        System.out.println("Expected second card balance: " + expectedSecondCardBalance);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance,
                "First card balance mismatch");
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance,
                "Second card balance mismatch");
    }

    @Test
    void shouldNotTransferMoneyWhenAmountExceedsBalance() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        System.out.println("Initial first card balance: " + initialFirstCardBalance);

        int transferAmount = initialFirstCardBalance + 1000;
        System.out.println("Transfer amount (exceeds balance): " + transferAmount);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        System.out.println("Actual first card balance after invalid transfer: " + actualFirstCardBalance);

        // Баланс не должен измениться
        assertEquals(initialFirstCardBalance, actualFirstCardBalance,
                "Balance should not change when transfer exceeds balance");
    }

    @Test
    void shouldNotTransferMoneyToSameCard() {
        String firstCardNumber = DataHelper.getFirstCardNumber();

        int initialBalance = dashboardPage.getCardBalance(firstCardNumber);
        System.out.println("Initial balance: " + initialBalance);

        int transferAmount = DataHelper.generateValidTransferAmount(initialBalance);
        System.out.println("Transfer amount (to same card): " + transferAmount);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(firstCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        int actualBalance = dashboardPage.getCardBalance(firstCardNumber);
        System.out.println("Actual balance after transfer to same card: " + actualBalance);

        assertEquals(initialBalance, actualBalance,
                "Balance should not change when transferring to same card");
    }

    @Test
    void shouldNotTransferZeroAmount() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        System.out.println("Initial balances: " + initialFirstCardBalance + ", " + initialSecondCardBalance);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(0, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        System.out.println("After zero transfer: " + actualFirstCardBalance + ", " + actualSecondCardBalance);

        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferNegativeAmount() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        System.out.println("Initial balances: " + initialFirstCardBalance + ", " + initialSecondCardBalance);

        int transferAmount = -100;
        System.out.println("Transfer amount (negative): " + transferAmount);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        System.out.println("After negative transfer: " + actualFirstCardBalance + ", " + actualSecondCardBalance);

        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferAllMoneyToAnotherCard() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        System.out.println("=== Initial balances ===");
        System.out.println("First card balance: " + initialFirstCardBalance);
        System.out.println("Second card balance: " + initialSecondCardBalance);

        int transferAmount = initialFirstCardBalance;
        System.out.println("Transfer amount (all money): " + transferAmount);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        DashboardPage newDashboardPage = transferPage.makeTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        System.out.println("=== After transfer all money ===");
        System.out.println("Actual first card balance: " + actualFirstCardBalance);
        System.out.println("Actual second card balance: " + actualSecondCardBalance);

        // Ожидаем, что на первой карте 0
        assertEquals(0, actualFirstCardBalance, "First card should be 0 after transferring all money");
        assertEquals(initialSecondCardBalance + transferAmount, actualSecondCardBalance);
    }
}