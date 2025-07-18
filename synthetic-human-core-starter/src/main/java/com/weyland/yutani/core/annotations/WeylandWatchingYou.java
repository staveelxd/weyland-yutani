package com.weyland.yutani.core.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeylandWatchingYou {

    /**
     * Описание того, что делает метод, который будет включен в журнал аудита.
     * Это помогает создать контекст для проверяемой операции.
     *
     * @return Описание проверяемой операции
     */
    String value() default "";

    /**
     *  Категория события аудита.
     *  Может использоваться для фильтрации или группировки связанных событий аудита.
     *
     * @return Категория события аудита
     */
    String category() default "default";

    /**
     * Уровень серьезности события аудита.
     * Может использоваться для фильтрации или выделения важных событий в журнале аудита.
     *
     * @return Уровень серьезности события аудита
     */
    Severity severity() default Severity.INFO;

    enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        INFO
    }
}
