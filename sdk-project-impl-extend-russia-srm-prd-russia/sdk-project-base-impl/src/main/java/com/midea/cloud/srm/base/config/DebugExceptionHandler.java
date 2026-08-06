package com.midea.cloud.srm.base.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // Максимальный приоритет, чтобы перехватить ошибку первым
public class DebugExceptionHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public String handleThrowable(Throwable t) {
        System.err.println("\n\n========== НАЙДЕНА РЕАЛЬНАЯ ОШИБКА ==========");
        t.printStackTrace(); // Выведет полный, читаемый стек-трейс в консоль IDEA!
        System.err.println("====================================================\n\n");

        // Возвращаем строку. GlobalHandleFilter её зашифрует и отдаст в Postman,
        // но нам это уже не важно, ведь настоящая причина напечатана в консоли выше!
        return "Смотри ошибка: " + t.getMessage();
    }
}
