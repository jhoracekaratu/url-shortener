package com.horace.url_shortener.controller;
import com.horace.url_shortener.dto.UrlShorteningRequest;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/urls")
@Tag(name = "URL Shortener", description = "API for shortening URLs and retrieving the original URL")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @Operation(summary = "Shorten a URL", description = "Takes a long URL and returns a shortened version of it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL shortened successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Url.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/shorten")
    public ResponseEntity<Url> shortenUrl(@Valid @RequestBody UrlShorteningRequest request) {
        User user = new User();
        user.setId(request.getUserId());  // Simulating fetching the user

        Url url = urlService.shortenUrl(request.getUrl(), user);
        return ResponseEntity.ok(url);
    }

    @Operation(summary = "Get original URL", description = "Fetches the original URL based on the shortened URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Original URL retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getOriginalUrl(
            @Parameter(description = "The shortened URL", required = true) @PathVariable String shortUrl) {
        System.out.println(shortUrl);
        Optional<Url> url = urlService.getOriginalUrl(shortUrl);
        return url.map(value -> ResponseEntity.ok(value.getOriginalUrl()))
                .orElse(ResponseEntity.notFound().build());
    }
}
