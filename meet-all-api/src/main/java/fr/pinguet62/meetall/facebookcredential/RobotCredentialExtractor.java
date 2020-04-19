package fr.pinguet62.meetall.facebookcredential;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Facebook login page
        driver.get(facebookOauthUrl);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.cssSelector("[type=submit]")).click();

        // Authorization page
        driver.findElement(By.id("platformDialogForm")).findElement(By.name("__CONFIRM__")).click();
        // Authorized page
        String html = driver.getPageSource();

        // parse HTML & extract query parameter from redirect URL
        final String before = "access_token=";
        final String after = "&data_access_expiration_time=";
        Matcher matcher = Pattern.compile(before + ".*" + after).matcher(html);
        matcher.find();
        String urlPart = matcher.group();
        String token = urlPart.replace(before, "").replace(after, "");

        return Mono.just(token);
    }

}
