package serviceSS.repository;

import serviceSS.entity.Link;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository {
    Link save(Link link); // Сохранить ссылку
    Optional<Link> findById(UUID id); // Найти ссылку по ID
    Map<UUID, Link> findAll(); // Найти все ссылки
    void delete(UUID id); // Удалить ссылку
    void deleteExpiredLinks(LocalDateTime currentDate);
    Optional<Link> findByShortLink(String shortLink);
}