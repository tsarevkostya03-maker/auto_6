package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private final ElementsCollection cards = $$("[data-test-id='card']");

    public int getCardBalance(String cardNumber) {
        SelenideElement card = findCardByNumber(cardNumber);
        String balanceText = card.$(".balance").getText()
                .replaceAll("[^\\d]", "");
        return Integer.parseInt(balanceText);
    }

    public TransferPage selectCardForReplenish(String cardNumber) {
        SelenideElement card = findCardByNumber(cardNumber);
        card.$("[data-test-id='action-deposit']").click();
        return new TransferPage();
    }

    private SelenideElement findCardByNumber(String cardNumber) {
        // Ищем карту по тексту номера, используя Condition.text()
        return cards.find(Condition.text(cardNumber));
    }
}