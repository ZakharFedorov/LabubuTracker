package sasa.checker;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import sasa.util.CookieLoader;

import java.io.File;
import java.time.Duration;
import java.util.Set;

public class PopmartChecker implements StockChecker {

    private static final String PRODUCT_URL = "https://www.popmart.com/en-CZ/products/527/THE-MONSTERS---Exciting-Macaron-Vinyl-Face-Blind-Box";
    private static final String COOKIES_PATH = "cookies-popmart.json";

    @Override
    public boolean isAvailable() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://www.popmart.com");

            File cookieFile = new File(COOKIES_PATH);
            if (cookieFile.exists()) {
                Set<Cookie> cookies = CookieLoader.loadCookiesFromFile(COOKIES_PATH);
                for (Cookie cookie : cookies) {
                    driver.manage().addCookie(cookie);
                }
                driver.navigate().to(PRODUCT_URL);
            } else {
                driver.navigate().to(PRODUCT_URL);
            }

            Thread.sleep(4000);

            try {
                WebElement unavailableElement = driver.findElement(
                        By.xpath("//*[contains(text(), 'NOTIFY ME WHEN AVAILABLE')]")
                );

                System.out.println("\uD83D\uDD0D Класс варианта:" + unavailableElement.getAttribute("outerHTML"));
                return false;
            } catch (NoSuchElementException e) {
                System.out.println("✅ Товар Popmart доступен.");
                return true;
            }

        } catch (Exception e) {
            System.out.println("❌ Ошибка при проверке товара на Popmart: " + e.getMessage());
            return false;

        } finally {
            driver.quit();
        }
    }

    @Override
    public String getProductName() {
        return "Labubu Macaron на Popmart";
    }

    @Override
    public String getProductUrl() {
        return PRODUCT_URL;
    }
}
