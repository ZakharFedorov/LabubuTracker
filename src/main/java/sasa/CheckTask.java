package sasa;

import java.util.TimerTask;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import java.util.Set;

class CheckTask extends TimerTask {

    private static final String PRODUCT_URL = "https://www.aliexpress.com/item/1005006169948468.html";

    static final String TELEGRAM_BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");
    static final String TELEGRAM_CHAT_ID = System.getenv("TELEGRAM_CHAT_ID");

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://www.aliexpress.com");
            Set<Cookie> cookies = App.loadCookiesFromFile("cookies.json");
            for (Cookie cookie : cookies) {
                driver.manage().addCookie(cookie);
            }

            driver.navigate().to(PRODUCT_URL);
            Thread.sleep(5000);

            String pageSource = driver.getPageSource().toLowerCase();
            String title = driver.getTitle().toLowerCase();

            if (pageSource.contains("geetest") || pageSource.contains("captcha") || title.contains("verify")) {
                System.out.println("🛑 Обнаружена капча.");
                App.sendTelegramMessage("⚠️ AliExpress требует капчу. Проверь cookies.");
                driver.quit();
                return;
            }

            try {
                WebElement firstVariant = driver.findElement(By.cssSelector("div[data-sku-col]"));
                String classAttr = firstVariant.getAttribute("class").toLowerCase();
                System.out.println("🔎 Класс варианта: " + classAttr);

                firstVariant.click();
                Thread.sleep(2000);

                if (classAttr.contains("soldout")) {
                    System.out.println("❌ Labubu распродан.");
                } else {
                    System.out.println("🎉 Labubu доступен!");
                    App.sendTelegramMessage("🎉 Labubu на AliExpress снова в наличии!");
                }
            } catch (NoSuchElementException e) {
                System.out.println("⚠️ Первый вариант товара не найден.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
