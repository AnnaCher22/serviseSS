package serviceSS.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Link {
    private UUID id; // Уникальный идентификатор ссылки
    private UUID userUUID; // Идентификатор пользователя
    private String longLink; // Оригинальный URL
    private String shortLink; // Короткий URL
    private int clicksLeft; // Остаток кликов
    private int currentClicks; // Текущие клики
    private LocalDateTime expirationDate; // Дата истечения

    public boolean canBeVisited() {
        return clicksLeft > 0 && expirationDate.isAfter(LocalDateTime.now());
    }

    public void incrementClicks() {
        if (canBeVisited()) {
            currentClicks++;
            clicksLeft--;
        } else {
            throw new RuntimeException("Ссылка недоступна для увеличения кликов");
        }
    }

    public void updateClicks(int maxClicks) {
        this.clicksLeft = maxClicks;
        this.currentClicks = 0;
    }

    public void updateExpiration(int days) {
        this.expirationDate = LocalDateTime.now().plusDays(days);
    }
}
