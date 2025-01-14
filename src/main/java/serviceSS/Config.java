package serviceSS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    // Чтение значения по умолчанию для срока действия ссылки из application.properties
    @Value("${link.default.expiration.days}")
    private int defaultExpirationDays;

    @Value("${link.default.max.clicks}")
    private int defaultMaxClicks;

    public int getDefaultExpirationDays() {
        return defaultExpirationDays; // Возвращаем нестатическое поле
    }

    public int getDefaultMaxClicks() {
        return defaultMaxClicks; // Возвращаем нестатическое поле
    }
}