package sasa;

import com.google.gson.*;
import org.openqa.selenium.Cookie;

import java.io.FileReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import static sasa.CheckTask.TELEGRAM_BOT_TOKEN;
import static sasa.CheckTask.TELEGRAM_CHAT_ID;

public class App {
    public static void main(String[] args) {
        // Для теста отправим сообщение сразу:
        sendTelegramMessage("✅ Привет! Бот успешно запущен.");

        Timer timer = new Timer();
        timer.schedule(new CheckTask(), 0, 10 * 60 * 1000); // каждые 10 минут
    }

    public static Set<Cookie> loadCookiesFromFile(String path) {
        Set<Cookie> cookies = new HashSet<>();
        try {
            JsonElement json = JsonParser.parseReader(new FileReader(path));
            for (JsonElement elem : json.getAsJsonArray()) {
                JsonObject obj = elem.getAsJsonObject();
                Cookie cookie = new Cookie.Builder(
                        obj.get("name").getAsString(),
                        obj.get("value").getAsString()
                ).domain(obj.get("domain").getAsString())
                        .path(obj.get("path").getAsString())
                        .isSecure(obj.get("secure").getAsBoolean())
                        .build();
                cookies.add(cookie);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Ошибка загрузки cookies: " + e.getMessage());
        }
        return cookies;
    }

    public static void sendTelegramMessage(String message) {
        try {
            String urlString = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "chat_id=" + TELEGRAM_CHAT_ID + "&text=" + message;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            // 🔍 Проверка кода ответа
            int responseCode = conn.getResponseCode();
            System.out.println("🔄 Response code: " + responseCode);

            // Читаем ответ как строку
            java.io.InputStream inputStream = conn.getInputStream();
            java.util.Scanner scanner = new java.util.Scanner(inputStream, "UTF-8");
            String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();

            System.out.println("📬 Ответ Telegram API: " + response);

        } catch (Exception e) {
            System.out.println("❌ Ошибка отправки в Telegram:");
            e.printStackTrace();
        }
    }
}
