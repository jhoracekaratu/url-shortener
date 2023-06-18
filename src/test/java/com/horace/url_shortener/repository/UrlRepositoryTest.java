package com.horace.url_shortener.repository;
import com.horace.url_shortener.entity.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    private Url testUrl;

    @BeforeEach
    void setUp() {
        testUrl = Url.builder()
                .originalUrl("https://test.com")
                .shortUrl("abc123")
                .build();
        urlRepository.save(testUrl); // Save a URL before each test
    }

    @Test
    void findByShortUrl_shouldReturnUrl_whenShortUrlExists() {
        Optional<Url> result = urlRepository.findByShortUrl("abc123");

        assertTrue(result.isPresent(), "Url should be found by its short URL");
        assertThat(result.get().getOriginalUrl()).isEqualTo("https://test.com");
    }

    @Test
    void findByShortUrl_shouldReturnEmpty_whenShortUrlDoesNotExist() {
        Optional<Url> result = urlRepository.findByShortUrl("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByOriginalUrl_shouldReturnUrl_whenOriginalUrlExists() {
        Optional<Url> result = urlRepository.findByOriginalUrl("https://test.com");

        assertTrue(result.isPresent(), "Url should be found by its original URL");
        assertThat(result.get().getShortUrl()).isEqualTo("abc123");
    }

    @Test
    void existsByShortUrl_shouldReturnTrue_whenShortUrlExists() {
        boolean exists = urlRepository.existsByShortUrl("abc123");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByShortUrl_shouldReturnFalse_whenShortUrlDoesNotExist() {
        boolean exists = urlRepository.existsByShortUrl("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void deleteUrl_shouldRemoveUrl() {
        urlRepository.delete(testUrl);
        Optional<Url> result = urlRepository.findByShortUrl("abc123");

        assertThat(result).isEmpty();
    }
}
