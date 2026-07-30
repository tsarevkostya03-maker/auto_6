package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

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
        // Given
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        int transferAmount = DataHelper.generateValidTransferAmount(initialFirstCardBalance);

        // When
        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        DashboardPage newDashboardPage = transferPage.makeTransfer(transferAmount, firstCardNumber);

        // Then
        int expectedFirstCardBalance = initialFirstCardBalance - transferAmount;
        int expectedSecondCardBalance = initialSecondCardBalance + transferAmount;
        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoneyWhenAmountExceedsBalance() {
        // Given
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int transferAmount = initialFirstCardBalance + 1000;

        // When
        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        // Then
        DashboardPage newDashboardPage = new DashboardPage();
        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        // Балансы не должны измениться (ошибка перевода)
        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(DataHelper.getInitialCardBalance(), actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoneyToSameCard() {
        // Given
        String firstCardNumber = DataHelper.getFirstCardNumber();
        int initialBalance = dashboardPage.getCardBalance(firstCardNumber);
        int transferAmount = DataHelper.generateValidTransferAmount(initialBalance);

        // When
        TransferPage transferPage = dashboardPage.selectCardForReplenish(firstCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        // Then
        DashboardPage newDashboardPage = new DashboardPage();
        int actualBalance = newDashboardPage.getCardBalance(firstCardNumber);

        // Баланс не должен измениться (ошибка перевода на ту же карту)
        assertEquals(initialBalance, actualBalance);
    }

    @Test
    void shouldNotTransferZeroAmount() {
        // Given
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        int transferAmount = 0;

        // When
        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        // Then
        DashboardPage newDashboardPage = new DashboardPage();
        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        // Балансы не должны измениться (ошибка перевода нулевой суммы)
        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferNegativeAmount() {
        // Given
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        int transferAmount = -100;

        // When
        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        transferPage.makeInvalidTransfer(transferAmount, firstCardNumber);

        // Then
        DashboardPage newDashboardPage = new DashboardPage();
        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        // Балансы не должны измениться (ошибка перевода отрицательной суммы)
        assertEquals(initialFirstCardBalance, actualFirstCardBalance);
        assertEquals(initialSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferAllMoneyToAnotherCard() {
        // Given
        String firstCardNumber = DataHelper.getFirstCardNumber();
        String secondCardNumber = DataHelper.getSecondCardNumber();
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCardNumber);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCardNumber);
        int transferAmount = initialFirstCardBalance;

        // When
        TransferPage transferPage = dashboardPage.selectCardForReplenish(secondCardNumber);
        DashboardPage newDashboardPage = transferPage.makeTransfer(transferAmount, firstCardNumber);

        // Then
        int expectedFirstCardBalance = 0;
        int expectedSecondCardBalance = initialSecondCardBalance + transferAmount;
        int actualFirstCardBalance = newDashboardPage.getCardBalance(firstCardNumber);
        int actualSecondCardBalance = newDashboardPage.getCardBalance(secondCardNumber);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }
}
