package com.horace.url_shortener.controller;

import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class UrlRedirectController {
    private final UrlService urlService;
    public UrlRedirectController(UrlService urlService){
        this.urlService=urlService;
    }

    @GetMapping("/{shortenedUrl}")
    public ResponseEntity<?> redirectToOriginal(@PathVariable String shortenedUrl, HttpServletResponse resp){
Optional<Url> originalUrlOpt =urlService.getOriginalUrl(shortenedUrl);
if(originalUrlOpt.isPresent()){
    Url url=originalUrlOpt.get();
    if (url.getExpiresAt() != null && LocalDateTime.now().isAfter(url.getExpiresAt())) {
        return new ResponseEntity<>("URL has expired", HttpStatus.GONE); // 410 Gone
    }
    try {
        resp.sendRedirect(url.getOriginalUrl());
        return new ResponseEntity<>(HttpStatus.FOUND);
    } catch (IOException e) {
        return  new ResponseEntity<>("Error during redirection"
                ,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}else{
    return new ResponseEntity<>("URL not found", HttpStatus.NOT_FOUND);

}
    }



}
