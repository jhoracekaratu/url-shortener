package com.horace.url_shortener.controller;

import com.horace.url_shortener.config.SecurityConfig;
import com.horace.url_shortener.dto.UrlShorteningRequest;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Import(SecurityConfig.class)
@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @MockBean
    private UrlService urlService;

    @Autowired
    private MockMvc mockMvc;

    private String testUrl = "https://test.com";
    private String testShortUrl = "dsfsd";
    private Url testUrlEntity;

    @BeforeEach
    public void setUp() {
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

        // Mocking the UrlService method
        when(urlService.shortenUrl(anyString(), any(User.class) )).thenReturn(testUrlEntity);

        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + testUrl + "\", \"userId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(testShortUrl))
                .andExpect(jsonPath("$.originalUrl").value(testUrl));

        // Verify the service method was called once
        verify(urlService, times(1)).shortenUrl(anyString(), any(User.class));
    }

    @Test
    public void shortenUrl_shouldReturnBadRequest_whenUrlIsInvalid() throws Exception {
        // Test for empty URL
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\", \"userId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("Invalid URL format")); // Adjust to match your error response

        // Test for invalid URL format
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid-url\", \"userId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("Invalid URL format")); // Adjust to match your error response

        // Verify that the service method is not called
        verify(urlService, never()).shortenUrl(any(String.class), any(User.class));
    }

    @Test
    public void getOriginalUrl_shouldReturnOriginalUrl_whenShortUrlExists() throws Exception {
        // Mocking the service to return the URL entity
        when(urlService.getOriginalUrl(testShortUrl)).thenReturn(Optional.of(testUrlEntity));

        mockMvc.perform(get("/api/urls/" + testShortUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(testUrl));

        // Verify the service method was called once
        verify(urlService, times(1)).getOriginalUrl(testShortUrl);
    }

    @Test
    public void getOriginalUrl_shouldReturnNotFound_whenShortUrlDoesNotExist() throws Exception {
        // Mocking the service to return an empty Optional
        when(urlService.getOriginalUrl(testShortUrl)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/urls/" + testShortUrl))
                .andExpect(status().isNotFound());

        // Verify the service method was called once
        verify(urlService, times(1)).getOriginalUrl(testShortUrl);
    }
}
