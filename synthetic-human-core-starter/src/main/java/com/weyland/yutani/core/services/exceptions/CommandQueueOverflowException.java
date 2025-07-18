package com.weyland.yutani.core.services.exceptions;

/**
 * Исключение, возникающее при переполнении очереди команд.
 * Генерируется, когда в очередь команд пытаются добавить новую команду,
 * но очередь уже достигла максимального размера.
 */
public class CommandQueueOverflowException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Очередь команд переполнена. Максимальный размер: %d";
    private final int maxQueueSize;

    /**
     * Создает новое исключение с сообщением по умолчанию.
     *
     * @param maxQueueSize максимальный размер очереди
     */
    public CommandQueueOverflowException(int maxQueueSize) {
        super(String.format(DEFAULT_MESSAGE, maxQueueSize));
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * Создает новое исключение с пользовательским сообщением.
     *
     * @param message подробное сообщение об ошибке
     */
    public CommandQueueOverflowException(String message) {
        super(message);
        this.maxQueueSize = -1;
    }

    /**
     * Создает новое исключение с пользовательским сообщением и причиной.
     *
     * @param message подробное сообщение об ошибке
     * @param cause   причина (которую сохраняют для последующего извлечения через getCause())
     */
    public CommandQueueOverflowException(String message, Throwable cause) {
        super(message, cause);
        this.maxQueueSize = -1;
    }

    /**
     * Возвращает максимальный размер очереди команд.
     *
     * @return максимальный размер очереди или -1, если не установлен
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
