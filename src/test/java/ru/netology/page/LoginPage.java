package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id='login'] input");
    private final SelenideElement passwordField = $("[data-test-id='password'] input");
    private final SelenideElement loginButton = $("[data-test-id='action-login']");

    public VerificationPage validLogin(DataHelper.AuthInfo authInfo) {
        System.out.println("Logging in with: " + authInfo.getLogin());
        loginField.setValue(authInfo.getLogin());
        passwordField.setValue(authInfo.getPassword());
        loginButton.click();
        sleep(2000);
        return new VerificationPage();
    }
}