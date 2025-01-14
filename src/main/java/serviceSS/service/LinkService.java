package serviceSS.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import serviceSS.entity.Link;
import serviceSS.repository.LinkRepository;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LinkService {

    private final LinkRepository linkRepository;

    @Value("${link.default.expiration.days}")
    private int defaultExpirationDays;

    @Value("${link.default.max.clicks}")
    private int defaultMaxClicks;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link createLink(String originalUrl, UUID userId, int maxClicks) {
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Некорректный URL: " + originalUrl);
        }

        // Логируем успешный вызов
        System.out.println("Создание ссылки для URL: " + originalUrl);

        int expirationDays = Math.min(defaultExpirationDays, maxClicks);
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(expirationDays);

        String shortLink = generateShortLink();

        Link link = Link.builder()
                .id(UUID.randomUUID())
                .userUUID(userId)
                .longLink(originalUrl)
                .shortLink(shortLink)
                .clicksLeft(maxClicks)
                .currentClicks(0)
                .expirationDate(expirationDate)
                .build();

        return linkRepository.save(link);
    }

    public String generateShortLink() {
        return "clck.ru/" + UUID.randomUUID().toString().substring(0, 6);
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public void updateClicks(UUID linkId, UUID userId, int newLimit) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Ссылка не найдена"));
        if (!link.getUserUUID().equals(userId)) {
            throw new RuntimeException("Нет прав для изменения лимита кликов");
        }
        link.setClicksLeft(newLimit);
        linkRepository.save(link);
    }

    public void deleteLink(UUID linkId, UUID userId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Ссылка не найдена"));
        if (!link.getUserUUID().equals(userId)) {
            throw new RuntimeException("Нет прав для удаления ссылки");
        }
        linkRepository.delete(linkId);
    }

    public Optional<Link> findById(UUID id) {
        return linkRepository.findById(id);
    }

    public Link incrementClickCount(UUID id) {
        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ссылка не найдена"));

        if (!link.canBeVisited()) {
            throw new RuntimeException("Ссылка недоступна. Лимит переходов исчерпан или ссылка истекла.");
        }

        link.incrementClicks();
        linkRepository.save(link);
        return link;
    }

    public void deleteExpiredLinks() {
        linkRepository.deleteExpiredLinks(LocalDateTime.now());
    }
}
