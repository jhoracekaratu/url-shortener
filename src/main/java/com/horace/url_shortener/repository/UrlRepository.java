package com.horace.url_shortener.repository;

import com.horace.url_shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortUrl(String shortUrl);

    Optional<Url> findByOriginalUrl(String originalUrl); // Method to find URL by original URL

    boolean existsByShortUrl(String shortUrl); // Check if a short URL already exists
}
