package fr.pinguet62.meetall.facebookcredential;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class RobotCredentialExtractor {

    private final String oauthServerUrl;

    @Autowired
    public RobotCredentialExtractor() {
        this("https://www.facebook.com");
    }

    RobotCredentialExtractor(String oauthServerUrl) {
        this.oauthServerUrl = oauthServerUrl;
    }

    public String getHappnFacebookToken(String email, String password) {
        return process(247294518656661L, List.of("email", "user_birthday", "user_photos", "user_friends", "public_profile", "user_likes"), email, password);
    }

    public String getOnceFacebookToken(String email, String password) {
        return process(629771890475899L, List.of("email", "user_birthday", "user_photos"), email, password);
    }

    public String getTinderFacebookToken(String email, String password) {
        return process(464891386855067L, List.of("email", "user_birthday", "user_photos", "user_education_history", "user_relationship_details", "user_friends", "user_work_history", "user_likes"), email, password);
    }

    private String process(long clientId, List<String> scopes, String email, String password) {
        String facebookOauthUrl = UriComponentsBuilder.fromUriString(oauthServerUrl)
                .path("/dialog/oauth")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "token")
                .queryParam("redirect_uri", "fb" + clientId + "://authorize/")
                .queryParam("scopes", scopes)
                .toUriString();

        // chromedriver config
        WebDriverManager.chromedriver().driverVersion("85.0.4183.83").setup(); // System.setProperty("webdriver.chrome.driver", "...");
        // Driver configuration
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(facebookOauthUrl);

            // Accept cookies
            try {
                driver.findElement(By.cssSelector("[data-cookiebanner=\"accept_button\"]")).click();
            } catch (NoSuchElementException e) {
            }

            // Facebook login page
            driver.findElement(By.id("email")).sendKeys(email);
            driver.findElement(By.id("pass")).sendKeys(password);
            driver.findElement(By.cssSelector("[type=submit]")).click();
            new WebDriverWait(driver, Duration.ofSeconds(1).toSeconds()).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("body.hasCookieBanner")));

            verifyCredentials(driver);

            verifyAbsentOrProcessAndThrowError(driver);

            // Authorization page
            List<WebElement> forms = driver.findElements(By.id("platformDialogForm"));
            forms.get(forms.size() - 1)
                    .findElement(By.name("__CONFIRM__")).click();

            // Authorized page
            String html = driver.getPageSource();

            return parseHtml(html);
        } catch (WebDriverException e) {
            throw new PageSourceWebDriverException(driver.getPageSource(), e);
        } finally {
            driver.close();
        }
    }

    /**
     * @throws InvalidCredentialsException L’e-mail entré ne correspond à aucun compte.
     * @throws InvalidCredentialsException Le mot de passe entré est incorrect. Vous l’avez oublié ?
     */
    private void verifyCredentials(WebDriver driver) {
        try {
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(1).toSeconds()).until(presenceOfElementLocated(By.cssSelector(".uiContextualLayer > div > div[role='alert']")));
            throw new InvalidCredentialsException(element.getText());
        } catch (TimeoutException e) {
        }
        try {
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(1).toSeconds()).until(presenceOfElementLocated(By.cssSelector("#error_box > div:first-child")));
            throw new InvalidCredentialsException(element.getText());
        } catch (TimeoutException e) {
        }
    }

    private void verifyAbsentOrProcessAndThrowError(WebDriver driver) {
        try {
            // step 1: "Veuillez confirmer votre identité"
            new WebDriverWait(driver, Duration.ofSeconds(1).toSeconds()).until(presenceOfElementLocated(By.id("checkpointBottomBar")));
        } catch (TimeoutException e) {
            return;
        }

        driver.findElement(By.id("checkpointSubmitButton")).click();
        // step 2: "Choisissez un contrôle de sécurité"
        new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(presenceOfAllElementsLocatedBy(By.className("uiInputLabelInput")));
        driver.findElements(By.className("uiInputLabelInput")) // <input> not clickable
                .get(0) // "Approuvez votre connexion sur un autre téléphone ou un autre ordinateur"
                .click();
        driver.findElement(By.id("checkpointSubmitButton")).click();
        // step 3
        new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(presenceOfElementLocated(By.id("checkpointFooterButton")));
        driver.findElement(By.id("checkpointSubmitButton")).click();

        throw new FacebookAccountLockedException();
    }

    static String parseHtml(String html) {
        Document document = Jsoup.parse(html);
        String script = document.select("html > head > script:first-child").html();

        String redirectUrl = script.replace("window.location.href=\"", "").replaceFirst("\";$", "")
                .replace("\\/", "/");
        URI uri = URI.create(redirectUrl);

        List<NameValuePair> fragments = URLEncodedUtils.parse(uri.getFragment(), UTF_8);
        return fragments.stream()
                .filter(it -> it.getName().equals("access_token"))
                .findAny()
                .orElseThrow()
                .getValue();
    }

}
