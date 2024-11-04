package com.horace.url_shortener.integration;


import com.horace.url_shortener.config.SecurityConfig;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.repository.UrlRepository;
import com.horace.url_shortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UrlRedirectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlService urlService;

    private String testOriginalUrl = "https://test.com";
    private String testShortUrl = "dsfsd";
    private String endpoint = "/redirect/dsfsd";
    private Url testUrl;

    @BeforeEach
    public void setUp() {
        urlRepository.deleteAll(); // Clear database before each test

        testUrl = Url.builder()
                .originalUrl(testOriginalUrl)
                .shortUrl(testShortUrl)
                .expiresAt(LocalDateTime.now().plusDays(1)) // Valid expiration date
                .build();
        urlRepository.save(testUrl);
    }

    @Test
    public void redirectToOriginal_shouldRedirectToOriginal() throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(testOriginalUrl));
    }

    @Test
    public void redirectToOriginal_shouldReturnNotFoundIfUrlDoesNotExist() throws Exception {
        // Ensure the repository is empty
        urlRepository.deleteAll();

        mockMvc.perform(get(endpoint))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("URL not found"));
    }

    @Test
    public void redirectToOriginal_shouldReturnGoneIfUrlIsExpired() throws Exception {
        // Expire the URL
        testUrl.setExpiresAt(LocalDateTime.now().minusHours(1));
        urlRepository.save(testUrl);

        mockMvc.perform(get(endpoint))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$").value("URL has expired"));
    }
}

