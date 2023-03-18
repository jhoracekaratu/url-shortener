package com.horace.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlShorteningRequest {
    private Long userId;
    @NotBlank(message = "Invalid URL format")
    @Pattern(
            regexp = "^(https?|ftp)://[\\w.-]+(?:\\.[a-zA-Z]{2,})(:[0-9]+)?(/.*)?$",
            message = "Invalid URL format"
    )



    private String url;
}

