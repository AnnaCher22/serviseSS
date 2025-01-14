package serviceSS.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import serviceSS.entity.Link;
import serviceSS.repository.LinkRepository;
import serviceSS.service.LinkService;

import java.awt.*;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Controller
public class LinkController {

    private final LinkService linkService;
    private final LinkRepository linkRepository;

    @Value("${link.default.max.clicks}")
    private int defaultMaxClicks;

    public LinkController(LinkService linkService, LinkRepository linkRepository) {
        this.linkService = linkService;
        this.linkRepository = linkRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createLink(@RequestBody String originalUrl) {
        if (originalUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Оригинальный URL не может быть пустым.");
        }

        int maxClicks = defaultMaxClicks;
        UUID userId = UUID.randomUUID();

        try {
            Link newLink = linkService.createLink(originalUrl, userId, maxClicks);
            return ResponseEntity.ok("Созданная короткая ссылка: " + newLink.getShortLink());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<String> redirectToLongUrl(@PathVariable String shortLink) {
        try {
            Optional<Link> linkOptional = linkRepository.findByShortLink(shortLink);
            Link link = linkOptional.orElseThrow(() -> new RuntimeException("Ссылка не найдена"));

            linkService.incrementClickCount(link.getId());

            // Попытка открыть ссылку в браузере
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(link.getLongLink()));
            }

            return ResponseEntity.ok("Переход на URL: " + link.getLongLink());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @PatchMapping("/link/{linkId}/update-clicks")
    public ResponseEntity<String> updateClicks(@PathVariable UUID linkId, @RequestParam UUID userId, @RequestParam int newLimit) {
        try {
            linkService.updateClicks(linkId, userId, newLimit);
            return ResponseEntity.ok("Лимит кликов успешно обновлен.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/link/{linkId}/delete")
    public ResponseEntity<String> deleteLink(@PathVariable UUID linkId, @RequestParam UUID userId) {
        try {
            linkService.deleteLink(linkId, userId);
            return ResponseEntity.ok("Ссылка успешно удалена.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/link/{shortLink}")
    public ResponseEntity<Link> getLinkByShortLink(@PathVariable String shortLink) {
        Optional<Link> linkOptional = linkRepository.findByShortLink(shortLink);
        Link link = linkOptional.orElseThrow(() -> new RuntimeException("Ссылка не найдена"));
        return ResponseEntity.ok(link);
    }
}
