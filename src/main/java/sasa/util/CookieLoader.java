package sasa.util;

import com.google.gson.*;
import org.openqa.selenium.Cookie;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class CookieLoader {

    public static Set<Cookie> loadCookiesFromFile(String path) {
        Set<Cookie> cookies = new HashSet<>();
        try {
            JsonElement json = JsonParser.parseReader(new FileReader(path));
            for (JsonElement elem : json.getAsJsonArray()) {
                JsonObject obj = elem.getAsJsonObject();
                Cookie cookie = new Cookie.Builder(
                        obj.get("name").getAsString(),
                        obj.get("value").getAsString()
                )
                        .domain(obj.get("domain").getAsString())
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
}
