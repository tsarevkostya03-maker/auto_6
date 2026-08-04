package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private final SelenideElement amountField = $("[data-test-id='amount'] input");
    private final SelenideElement fromCardField = $("[data-test-id='from'] input");
    private final SelenideElement transferButton = $("[data-test-id='action-transfer']");

    // Основной метод для перевода
    public DashboardPage makeTransfer(int amount, String fromCardNumber) {
        fillTransferForm(amount, fromCardNumber);
        transferButton.click();
        return new DashboardPage();
    }

    // Метод для некорректных переводов (без возврата на Dashboard)
    public void makeInvalidTransfer(int amount, String fromCardNumber) {
        fillTransferForm(amount, fromCardNumber);
        transferButton.click();
    }

    // Вынесенный общий метод для заполнения формы
    private void fillTransferForm(int amount, String fromCardNumber) {
        amountField.setValue(String.valueOf(amount));
        fromCardField.setValue(fromCardNumber);
    }
}