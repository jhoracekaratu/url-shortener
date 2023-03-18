package com.horace.url_shortener.service;

import com.horace.url_shortener.config.UrlShortenerConfig;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlShortenerConfig urlShortenerConfig;

    public UrlService(UrlRepository urlRepository, UrlShortenerConfig urlShortenerConfig) {
        this.urlRepository = urlRepository;
        this.urlShortenerConfig = urlShortenerConfig;
    }

    public Url shortenUrl(String originalUrl, User user) {
        // Validate input parameters
        if (originalUrl == null) {
            throw new IllegalArgumentException("Original URL cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Optional<Url> existingUrlOpt = urlRepository.findByOriginalUrl(originalUrl);
        Url urlToSave;

        if (existingUrlOpt.isPresent()) {
            urlToSave = existingUrlOpt.get();
        } else {
            urlToSave = new Url();
            urlToSave.setOriginalUrl(originalUrl);
            urlToSave.setUser(user);
        }

        String newShortUrl;
        do {
            System.out.println("first");
            newShortUrl = generateShortUrl();
        } while (urlRepository.existsByShortUrl(newShortUrl) || newShortUrl.equals(urlToSave.getShortUrl()));

        urlToSave.setShortUrl(newShortUrl);
        urlToSave.setExpiresAt(LocalDateTime.now().plusHours(urlShortenerConfig.getExpirationHours()));

        return urlRepository.save(urlToSave);
    }

    public Optional<Url> getOriginalUrl(String shortUrlCode) {
        if(shortUrlCode.isEmpty()){
            throw new IllegalArgumentException("Short url cannot be empty");
        }
        return urlRepository.findByShortUrl(shortUrlCode);
    }

    private String generateShortUrl() {
        String base62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrl = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(base62Chars.length());
            shortUrl.append(base62Chars.charAt(index));
        }

        return shortUrl.toString();
    }
}
