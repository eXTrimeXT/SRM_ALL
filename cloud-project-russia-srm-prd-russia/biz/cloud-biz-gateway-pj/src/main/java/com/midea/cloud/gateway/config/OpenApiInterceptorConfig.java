package com.midea.cloud.gateway.config;

import com.midea.cloud.common.open.pub.holder.OpenApiInterceptorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenApiInterceptorConfig {

    /**
     * Создаем Bean вручную, так как Spring не может найти его в зависимостях.
     * Это позволит удовлетворить требование @Autowired в OpenApiV1AccessFilter
     */
    @Bean
    @Primary
    public OpenApiInterceptorHandler openApiInterceptorHandler() {
        // Возвращаем новый экземпляр.
        // Если это интерфейс, вам нужно будет создать класс, реализующий его,
        // но судя по декомпилированному коду, это скорее всего обычный класс.
        return new OpenApiInterceptorHandler();
    }
}