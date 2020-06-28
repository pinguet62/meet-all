package fr.pinguet62.meetall.facebookcredential;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static java.util.Objects.requireNonNull;

class PageSourceWebDriverException extends WebDriverException {

    @Getter
    private final String pageSource;

    /**
     * @param pageSource {@link WebDriver#getPageSource()}
     */
    PageSourceWebDriverException(String pageSource, WebDriverException cause) {
        super("Error processing page", cause);
        this.pageSource = requireNonNull(pageSource);
    }
}
