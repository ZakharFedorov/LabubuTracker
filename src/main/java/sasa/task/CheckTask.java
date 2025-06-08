package sasa.task;

import sasa.checker.StockChecker;
import sasa.util.TelegramNotifier;

import java.util.TimerTask;

public class CheckTask extends TimerTask {

    private final StockChecker checker;

    public CheckTask(StockChecker checker) {
        this.checker = checker;
    }

    @Override
    public void run() {
        try {
            System.out.println("🔄 Проверка: " + checker.getProductName());
            if (checker.isAvailable()) {
                TelegramNotifier.send("🎉 В наличии: " + checker.getProductName() + "\n" + checker.getProductUrl());
            } else {
                System.out.println("❌ " + checker.getProductName() + " пока недоступен.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при выполнении проверки для " + checker.getProductName());
            e.printStackTrace();
        }
    }
}
