package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class VerificationPage {
    private final SelenideElement codeField = $("[data-test-id='code'] input");
    private final SelenideElement verifyButton = $("[data-test-id='action-verify']");

    public DashboardPage validVerify(DataHelper.VerificationCode verificationCode) {
        System.out.println("Entering code: " + verificationCode.getCode());
        codeField.setValue(verificationCode.getCode());
        verifyButton.click();
        sleep(2000);
        return new DashboardPage();
    }
}