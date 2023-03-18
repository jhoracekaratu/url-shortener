package com.horace.url_shortener.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "urlshortener")
@Getter
@Setter
public class UrlShortenerConfig {
    private int expirationHours;
}
