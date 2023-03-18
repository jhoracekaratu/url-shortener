package com.horace.url_shortener.integration;


import com.horace.url_shortener.config.SecurityConfig;
import com.horace.url_shortener.dto.UrlShorteningRequest;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.entity.User;
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

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlService urlService;

    private String testUrl = "https://test.com";
    private String testShortUrl = "dsfsd";
    private Url testUrlEntity;

    @BeforeEach
    public void setUp() {
        urlRepository.deleteAll(); // Clear the database before each test

        // Initialize the Url object with test data
        testUrlEntity = new Url();
        testUrlEntity.setOriginalUrl(testUrl);
        testUrlEntity.setShortUrl(testShortUrl);
    }

    @Test
    public void shortenUrl_shouldReturnOk_whenUrlIsValid() throws Exception {
        UrlShorteningRequest request = new UrlShorteningRequest();
        request.setUrl(testUrl);
        request.setUserId(1L);

        // Simulate saving the shortened URL to the repository
        urlRepository.save(testUrlEntity);

        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + testUrl + "\", \"userId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    public void shortenUrl_shouldReturnBadRequest_whenUrlIsInvalid() throws Exception {
        // Test for empty URL
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\", \"userId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("Invalid URL format"));

        // Test for invalid URL format
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid-url\", \"userId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("Invalid URL format"));
    }

    @Test
    public void getOriginalUrl_shouldReturnOriginalUrl_whenShortUrlExists() throws Exception {
        // Simulate saving the shortened URL to the repository
        urlRepository.save(testUrlEntity);

        mockMvc.perform(get("/api/urls/" + testShortUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(testUrl));
    }

    @Test
    public void getOriginalUrl_shouldReturnNotFound_whenShortUrlDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/urls/" + testShortUrl))
                .andExpect(status().isNotFound());
    }
}

