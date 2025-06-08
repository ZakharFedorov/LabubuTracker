package sasa.checker;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import sasa.util.CookieLoader;

import java.io.File;
import java.time.Duration;
import java.util.Set;

public class AliExpressChecker implements StockChecker {

    private static final String PRODUCT_URL = "https://www.aliexpress.com/item/1005006169948468.html";
    private static final String COOKIES_PATH = "cookies-aliexpress.json";

    @Override
    public boolean isAvailable() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://www.aliexpress.com");

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

            Thread.sleep(4000); // подождать загрузку страницы

            String pageSource = driver.getPageSource().toLowerCase();
            String title = driver.getTitle().toLowerCase();

            if (pageSource.contains("geetest") || pageSource.contains("captcha") || title.contains("verify")) {
                System.out.println("🛑 Обнаружена капча на AliExpress.");
                return false;
            }

            WebElement variant = driver.findElement(By.cssSelector("div[data-sku-col]"));
            String classAttr = variant.getAttribute("class").toLowerCase();

            System.out.println("🔍 Класс варианта: " + classAttr);
            return !classAttr.contains("soldout");

        } catch (Exception e) {
            System.out.println("❌ Ошибка при проверке товара на AliExpress: " + e.getMessage());
            return false;
        } finally {
            driver.quit();
        }
    }

    @Override
    public String getProductName() {
        return "Labubu Macaron на AliExpress";
    }

    @Override
    public String getProductUrl() {
        return PRODUCT_URL;
    }
}
