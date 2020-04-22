package fr.pinguet62.meetall.facebookcredential;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.springframework.http.HttpStatus.LOCKED;

@Component
public class RobotCredentialExtractor {

    public Mono<String> getHappnFacebookToken(String email, String password) {
        return process(247294518656661L, List.of("email", "user_birthday", "user_photos", "user_friends", "public_profile", "user_likes"), email, password);
    }

    public Mono<String> getOnceFacebookToken(String email, String password) {
        return process(629771890475899L, List.of("email", "user_birthday", "user_photos"), email, password);
    }

    public Mono<String> getTinderFacebookToken(String email, String password) {
        return process(464891386855067L, List.of("email", "user_birthday", "user_photos", "user_education_history", "user_relationship_details", "user_friends", "user_work_history", "user_likes"), email, password);
    }

    private Mono<String> process(long clientId, List<String> scopes, String email, String password) {
        String facebookOauthUrl = UriComponentsBuilder.fromUriString("https://www.facebook.com/dialog/oauth")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "token")
                .queryParam("redirect_uri", "fb" + clientId + "://authorize/")
                .queryParam("scopes", scopes)
                .toUriString();

        // chromedriver config
        WebDriverManager.chromedriver().version("80.0.3987.106").setup(); // System.setProperty("webdriver.chrome.driver", "...");
        // Driver configuration
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver(options);

        try {
            // Facebook login page
            driver.get(facebookOauthUrl);
            driver.findElement(By.id("email")).sendKeys(email);
            driver.findElement(By.id("pass")).sendKeys(password);
            driver.findElement(By.cssSelector("[type=submit]")).click();

            checkVerificationNotPresentOrThrowError(driver);

            // Authorization page
            driver.findElement(By.id("platformDialogForm")).findElement(By.name("__CONFIRM__")).click();
        } catch (WebDriverException e) {
            throw new PageSourceWebDriverException(driver.getPageSource(), e);
        }

        // Authorized page
        String html = driver.getPageSource();

        return Mono.just(parseHtml(html));
    }

    private void checkVerificationNotPresentOrThrowError(WebDriver driver) {
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

        throw new ResponseStatusException(LOCKED, "Unauthorized IP: please accept it on your Facebook account");
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
