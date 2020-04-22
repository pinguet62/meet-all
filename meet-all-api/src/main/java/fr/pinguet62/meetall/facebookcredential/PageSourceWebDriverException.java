package fr.pinguet62.meetall.facebookcredential;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

class PageSourceWebDriverException extends WebDriverException {
    /**
     * @param pageSource {@link WebDriver#getPageSource()}
     */
    PageSourceWebDriverException(String pageSource, WebDriverException cause) {
        super("Error processing page: " + pageSource, cause);
    }
}
