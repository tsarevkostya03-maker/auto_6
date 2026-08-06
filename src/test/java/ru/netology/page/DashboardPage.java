package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    // Ищем элементы карт
    private final ElementsCollection cards = $$(".list__item");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        // Ожидаем появления хотя бы одной карты на странице
        cards.first().shouldBe(Condition.visible);
    }

    public int getCardBalance(String cardNumber) {
        SelenideElement card = findCardByNumber(cardNumber);
        String text = card.getText();
        return extractBalance(text);
    }

    public TransferPage selectCardForReplenish(String cardNumber) {
        SelenideElement card = findCardByNumber(cardNumber);
        card.$("[data-test-id='action-deposit']").click();
        return new TransferPage();
    }

    private SelenideElement findCardByNumber(String cardNumber) {
        String lastFourDigits = DataHelper.getLastFourDigits(cardNumber);
        return cards.find(Condition.text(lastFourDigits));
    }

    // Метод для извлечения баланса из текста (рекомендован в инструкции)
    private int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }
}