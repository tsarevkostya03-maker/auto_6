package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private final SelenideElement amountField = $("[data-test-id='amount'] input");
    private final SelenideElement fromCardField = $("[data-test-id='from'] input");
    private final SelenideElement transferButton = $("[data-test-id='action-transfer']");

    public DashboardPage makeTransfer(int amount, String fromCardNumber) {
        fillTransferForm(amount, fromCardNumber);
        transferButton.click();
        return new DashboardPage();
    }

    public void makeInvalidTransfer(int amount, String fromCardNumber) {
        fillTransferForm(amount, fromCardNumber);
        transferButton.click();
    }

    private void fillTransferForm(int amount, String fromCardNumber) {
        amountField.setValue(String.valueOf(amount));
        fromCardField.setValue(fromCardNumber);
    }
}