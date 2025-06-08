package sasa.checker;

public interface StockChecker {

    /**
     * Проверяет наличие товара на сайте.
     * @return true — если товар в наличии, false — если распродан или недоступен.
     */
    boolean isAvailable();

    /**
     * Возвращает название товара или описание.
     * Используется для сообщений в Telegram и логов.
     */
    String getProductName();

    /**
     * Возвращает прямую ссылку на товар.
     * Используется в уведомлениях.
     */
    String getProductUrl();
}
