package sasa;

import sasa.checker.AliExpressChecker;
import sasa.checker.PopmartChecker;
import sasa.task.CheckTask;
import sasa.util.TelegramNotifier;

import java.util.Timer;

public class App {
    public static void main(String[] args) {
        TelegramNotifier.send("✅ Бот запущен. Проверка товаров началась.");

        Timer timer = new Timer();

        timer.schedule(new CheckTask(new AliExpressChecker()), 0, 3 * 60 * 1000);
        timer.schedule(new CheckTask(new PopmartChecker()), 0, 3 * 60 * 1000);
    }
}
