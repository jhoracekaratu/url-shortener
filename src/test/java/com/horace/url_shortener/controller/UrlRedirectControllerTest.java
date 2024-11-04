package com.horace.url_shortener.controller;

import com.horace.url_shortener.config.SecurityConfig;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(UrlRedirectController.class)
class UrlRedirectControllerTest {
    @InjectMocks
    private UrlRedirectController urlRedirectController;
    @MockBean
    UrlService urlService;

    @Autowired
    MockMvc mockMvc;

    String testOriginalUrl="https://test.com";
    String testShortUrl="dsfsd";
    private String endpoint="/redirect/dsfsd";
    Url testUrl;

    @BeforeEach
    public void setUp(){
        testUrl= Url.builder()
                .originalUrl(testOriginalUrl)
                .shortUrl(testShortUrl)
                .build();
    }
    @Test
    public void redirectToOriginal_shouldRedirectToOriginal() throws Exception {

    when(urlService.getOriginalUrl(testShortUrl)).thenReturn(Optional.of(testUrl));
mockMvc.perform(get(endpoint))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(testOriginalUrl));
        verify(urlService,times(1)).getOriginalUrl(testShortUrl);
    }
    @Test
    public void  redirectToOriginal_shouldNotFindIfOriginalUrlIsEmpty() throws Exception {
        // Mocking service behavior when an empty short URL is passed
        when(urlService.getOriginalUrl(testShortUrl))
                .thenReturn(Optional.empty());

        // Perform request with an empty path ("/") or invalid path
        mockMvc.perform(get(endpoint)) // an empty path may not trigger the controller
                .andExpect(status().isNotFound()) // Adjust status to what your app returns for bad requests
                .andExpect(jsonPath("$").value("URL not found"));

        // Verify that the service was called exactly once with an empty string
        verify(urlService, times(1)).getOriginalUrl(testShortUrl);
    }

    @Test
    public void redirectToOriginal_shouldNotFindIfExpired() throws Exception {
        // Mocking service behavior when an empty short URL is passed
        testUrl.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(urlService.getOriginalUrl(testShortUrl))
                .thenReturn(Optional.of(testUrl));

        // Perform request with an empty path ("/") or invalid path
        mockMvc.perform(get(endpoint)) // an empty path may not trigger the controller
                .andExpect(status().isGone()) // Adjust status to what your app returns for bad requests
                .andExpect(jsonPath("$").value("URL has expired"));

        // Verify that the service was called exactly once with an empty string
        verify(urlService, times(1)).getOriginalUrl(testShortUrl);
    }





}