package com.horace.url_shortener.controller;


import com.horace.url_shortener.dto.UrlShorteningRequest;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Url> shortenUrl(@Valid  @RequestBody UrlShorteningRequest request) {
        // Get user from the userService or repository
        User user = new User();
        user.setId(request.getUserId());  // Simulating fetching the user

        Url url = urlService.shortenUrl(request.getUrl(),user);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String shortUrl) {
        System.out.println(shortUrl);
        Optional<Url> url = urlService.getOriginalUrl(shortUrl);
        return url.map(value -> ResponseEntity.ok(value.getOriginalUrl()))
                .orElse(ResponseEntity.notFound().build());
    }

}
