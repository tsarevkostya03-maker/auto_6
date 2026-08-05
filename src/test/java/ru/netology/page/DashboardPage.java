package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;

public class DashboardPage {
    private final ElementsCollection cards = $$(".list__item");

    public DashboardPage() {
        sleep(2000);
        System.out.println("Dashboard loaded. Cards found: " + cards.size());
        for (int i = 0; i < cards.size(); i++) {
            System.out.println("Card " + i + ": " + cards.get(i).getText());
        }
    }

    public int getCardBalance(String cardNumber) {
        SelenideElement card = findCardByNumber(cardNumber);
        String cardText = card.getText();
        System.out.println("Raw card text: '" + cardText + "'");
        return extractBalance(cardText);
    }

    public TransferPage selectCardForReplenish(String cardNumber) {
        SelenideElement card = findCardByNumber(cardNumber);
        card.$("[data-test-id='action-deposit']").click();
        return new TransferPage();
    }

    private SelenideElement findCardByNumber(String cardNumber) {
        String lastFourDigits = DataHelper.getLastFourDigits(cardNumber);
        System.out.println("Searching for card ending with: " + lastFourDigits);
        return cards.find(Condition.text(lastFourDigits));
    }

    private int extractBalance(String cardText) {
        // Ищем баланс в тексте с учетом возможной проблемы кодировки
        // "баланс:" или "срырэё:" (из-за кодировки)

        String balancePart = null;

        // Вариант 1: ищем "баланс:"
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("баланс:\\s*(-?\\d+)");
        java.util.regex.Matcher matcher1 = pattern1.matcher(cardText);
        if (matcher1.find()) {
            balancePart = matcher1.group(1);
        }

        // Вариант 2: ищем "срырэё:" (из-за кодировки)
        if (balancePart == null) {
            java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("срырэё:\\s*(-?\\d+)");
            java.util.regex.Matcher matcher2 = pattern2.matcher(cardText);
            if (matcher2.find()) {
                balancePart = matcher2.group(1);
            }
        }

        // Вариант 3: ищем любое число после запятой
        if (balancePart == null) {
            java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile(",\\s*(-?\\d+)");
            java.util.regex.Matcher matcher3 = pattern3.matcher(cardText);
            if (matcher3.find()) {
                balancePart = matcher3.group(1);
            }
        }

        // Вариант 4: ищем любое число в тексте
        if (balancePart == null) {
            java.util.regex.Pattern pattern4 = java.util.regex.Pattern.compile("-?\\d+");
            java.util.regex.Matcher matcher4 = pattern4.matcher(cardText);
            if (matcher4.find()) {
                balancePart = matcher4.group();
            }
        }

        if (balancePart != null) {
            try {
                return Integer.parseInt(balancePart);
            } catch (NumberFormatException e) {
                // Продолжаем дальше
            }
        }

        throw new NumberFormatException("Could not extract balance from: " + cardText);
    }
}