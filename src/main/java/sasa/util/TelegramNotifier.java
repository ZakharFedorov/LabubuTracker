package sasa.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TelegramNotifier {

    private static final String BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");

    private static final List<String> CHAT_IDS = List.of(
            System.getenv("TELEGRAM_CHAT_ID_1"),
            System.getenv("TELEGRAM_CHAT_ID_2")
    );

    public static void send(String message) {
        for (String chatId : CHAT_IDS) {
            try {
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                URL url = new URL(urlString);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String body = "chat_id=" + chatId + "&text=" + message;

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }

                conn.getInputStream().close();
                System.out.println("📬 Уведомление отправлено пользователю " + chatId);
            } catch (Exception e) {
                System.out.println("❌ Ошибка при отправке Telegram-сообщения пользователю " + chatId);
                e.printStackTrace();
            }
        }
    }
}
