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
        int transferAmount = DataHelper.generateValidTransferAmount(initialFirstCardBalance);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        DashboardPage newDashboardPage = transferPage.makeTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        assertEquals(initialFirstCardBalance - transferAmount, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance + transferAmount, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoneyWhenAmountExceedsBalance() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        int transferAmount = initialFirstCardBalance + 1000;

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        assertEquals(initialFirstCardBalance, actualFirstCardBalance,
                "Balance of first card should not change");
        assertEquals(initialSecondCardBalance, actualSecondCardBalance,
                "Balance of second card should not change");
    }

    @Test
    void shouldNotTransferMoneyToSameCard() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        int transferAmount = DataHelper.generateValidTransferAmount(initialFirstCardBalance);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(firstCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        assertEquals(initialFirstCardBalance, actualFirstCardBalance,
                "Balance of first card should not change when transferring to itself");
        assertEquals(initialSecondCardBalance, actualSecondCardBalance,
                "Balance of second card should not change");
    }

    @Test
    void shouldNotTransferZeroAmount() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(0, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferNegativeAmount() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(-100, firstCardNumber);

        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferAllMoneyToAnotherCard() {
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();

        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);

        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        DashboardPage newDashboardPage = transferPage.makeTransfer(initialFirstCardBalance, firstCardNumber);

        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        assertEquals(0, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance + initialFirstCardBalance, actualSecondCardBalance);
    }
}