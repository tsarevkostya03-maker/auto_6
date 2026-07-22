package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private final SelenideElement heading = $("[data-test-id='dashboard']");

    public int getCardBalance(String cardNumber) {
        String cardNumberMasked = maskCardNumber(cardNumber);
        String balanceText = $("[data-test-id='" + cardNumberMasked + "'] .balance")
                .getText()
                .replaceAll("[^\\d]", "");
        return Integer.parseInt(balanceText);
    }

    public TransferPage selectCardForReplenish(String cardNumber) {
        String cardNumberMasked = maskCardNumber(cardNumber);
        SelenideElement cardElement = $("[data-test-id='" + cardNumberMasked + "']");
        cardElement.$("[data-test-id='action-deposit']").click();
        return new TransferPage();
    }

    private String maskCardNumber(String cardNumber) {
        // Маскирование номера карты для data-test-id
        String digits = cardNumber.replaceAll("\\s", "");
        return "card-" + digits.substring(0, 4) + "-" + digits.substring(4, 8) + "-" + digits.substring(8, 12) + "-" + digits.substring(12);
    }
}