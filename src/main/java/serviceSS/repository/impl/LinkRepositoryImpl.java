package serviceSS.repository.impl;

import org.springframework.stereotype.Repository; // Импортируем аннотацию
import serviceSS.entity.Link;
import serviceSS.repository.LinkRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LinkRepositoryImpl implements LinkRepository {
    private final Map<UUID, Link> links = new HashMap<>();

    @Override
    public Link save(Link link) {
        UUID id = UUID.randomUUID(); // Генерация уникального ID
        link.setId(id); // Установка ID в ссылку
        links.put(id, link); // Сохранение ссылки в хранилище
        return link; // Возвращаем сохраненную ссылку
    }

    @Override
    public Optional<Link> findById(UUID id) {
        return Optional.ofNullable(links.get(id)); // Возвращаем опциональную ссылку
    }

    @Override
    public Map<UUID, Link> findAll() {
        return new HashMap<>(links); // Возвращаем копию всех ссылок
    }

    @Override
    public void delete(UUID id) {
        links.remove(id); // Удаление ссылки
    }

    @Override
    public void deleteExpiredLinks(LocalDateTime currentDate) {
        Iterator<Map.Entry<UUID, Link>> iterator = links.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Link> entry = iterator.next();
            Link link = entry.getValue();

            if (link.getExpirationDate() != null && link.getExpirationDate().isBefore(currentDate)) {
                iterator.remove(); // Удаляем ссылку, если она истекла
            }
        }
    }

    @Override
    public Optional<Link> findByShortLink(String shortLink) {
        return links.values().stream()
                .filter(link -> link.getShortLink().equals(shortLink))
                .findFirst(); // Возвращаем первую найденную ссылку
    }
}