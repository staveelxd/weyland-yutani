package com.weyland.yutani.core.services.exceptions;

/**
 * Исключение возникает при сбое команды во время выполнения.
 */
public class CommandExecutionException extends RuntimeException {

    /**
     * Создает новое исключение при выполнении команды с указанным подробным сообщением.
     *
     * @param message подробное сообщение
     */
    public CommandExecutionException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение при выполнении команды с указанным подробным сообщением и причиной.
     *
     * @param message подробное сообщение
     * @param cause причина
     */
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создает новое исключение при выполнении команды с указанным идентификатором команды и сообщением об ошибке.
     *
     * @param commandId идентификатор команды, которая завершилась ошибкой
     * @param errorMessage сообщение об ошибке
     * @return new CommandExecutionException
     */
    public static CommandExecutionException forCommand(String commandId, String errorMessage) {
        return new CommandExecutionException(String.format(
            "Не удалось выполнить команду %s: %s", commandId, errorMessage));
    }
}
