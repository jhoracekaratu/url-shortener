package com.horace.url_shortener.service;

import com.horace.url_shortener.config.UrlShortenerConfig;
import com.horace.url_shortener.entity.Url;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.repository.UrlRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlShortenerConfig urlShortenerConfig;

    @InjectMocks
    private UrlService urlService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L); // Assume User has an ID field
//        testUser.setUsername("testUser");
        when(urlShortenerConfig.getExpirationHours()).thenReturn(1); // Set a default expiration
    }

    private Url createUrl(String originalUrl, LocalDateTime now) {
        return Url.builder()
                .id(1L)
                .shortUrl("https://test.com")
                .originalUrl(originalUrl)
                .updatedAt(now)
                .createdAt(now)
                .user(testUser)
                .build();
    }

    private Url cloneUrl(Url urlToCopy) {
        return Url.builder()
                .id(urlToCopy.getId())
                .shortUrl(urlToCopy.getShortUrl())
                .originalUrl(urlToCopy.getOriginalUrl())
                .updatedAt(urlToCopy.getUpdatedAt())
                .createdAt(urlToCopy.getCreatedAt())
                .user(urlToCopy.getUser())
                .build();
    }

    @Test
    void shortenUrl_shouldCreateNewUrlIfNoneExists() {
        // Arrange
        String originalUrl = "https://www.test.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Url shortenedUrl = urlService.shortenUrl(originalUrl, testUser);

        // Assert
        assertAll("Validating shortened URL creation",
                () -> assertNotNull(shortenedUrl, "Shortened URL should not be null"),
                () -> assertEquals(originalUrl, shortenedUrl.getOriginalUrl(), "Original URL should match"),
                () -> assertNotNull(shortenedUrl.getShortUrl(), "Shortened URL should be generated"),
                () -> assertEquals(testUser, shortenedUrl.getUser(), "User should match"),
                () -> assertNotNull(shortenedUrl.getExpiresAt(), "Expiration should be set"),
                () -> assertNull(shortenedUrl.getUpdatedAt(), "UpdatedAt should be null for new URLs")
        );
        verify(urlRepository, times(1)).save(argThat(savedUrl ->
                savedUrl.getOriginalUrl().equals(originalUrl) &&
                        savedUrl.getUser().equals(testUser)
        ));
    }

    @Test
    void shortenUrl_shouldUpdateUrlIfExists() {
        // Arrange
        String originalUrl = "https://www.test.com";
        Url existingUrl = createUrl(originalUrl, LocalDateTime.now());
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));
        when(urlRepository.save(any(Url.class))).thenReturn(existingUrl);

        // Act
        Url updatedUrl = urlService.shortenUrl(originalUrl, testUser);

        // Assert
        assertAll("Validating URL update",
                () -> assertNotNull(updatedUrl, "Updated URL should not be null"),
                () -> assertEquals(originalUrl, updatedUrl.getOriginalUrl(), "Original URL should match"),
                () -> assertEquals(existingUrl.getShortUrl(), updatedUrl.getShortUrl(), "Shortened URL should remain the same"),
                () -> assertEquals(testUser, updatedUrl.getUser(), "User should match"),
                () -> assertNotNull(updatedUrl.getExpiresAt(), "Expiration should be set"),
                () -> assertNotNull(updatedUrl.getUpdatedAt(), "UpdatedAt should not be null for existing URLs")
        );
        verify(urlRepository, times(1)).save(updatedUrl);
    }

    @Test
    void shortenUrl_shouldGenerateNewShortUrlIfConflictOccurs() {
        // Arrange
        String originalUrl = "https://www.test.com";
        Url existingUrl = createUrl(originalUrl, LocalDateTime.now());
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));

        String conflictingShortUrl = "https://www.conflictingshorturl.com";
        existingUrl.setShortUrl(conflictingShortUrl);
        when(urlRepository.existsByShortUrl(conflictingShortUrl)).thenReturn(true);

        String newShortUrl = "https://www.newshorturl.com";
        when(urlRepository.existsByShortUrl(newShortUrl)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setShortUrl(newShortUrl); // Simulate assigning the new short URL
            return url;
        });

        // Act
        Url shortenedUrl = urlService.shortenUrl(originalUrl, testUser);

        // Assert
        assertAll("Validating new short URL generation on conflict",
                () -> assertNotNull(shortenedUrl, "Shortened URL should not be null"),
                () -> assertEquals(originalUrl, shortenedUrl.getOriginalUrl(), "Original URL should match"),
                () -> assertEquals(newShortUrl, shortenedUrl.getShortUrl(), "A new Shortened URL should be generated"),
                () -> assertEquals(testUser, shortenedUrl.getUser(), "User should match"),
                () -> assertNotNull(shortenedUrl.getExpiresAt(), "Expiration should be set"),
                () -> assertNotNull(shortenedUrl.getUpdatedAt(), "UpdatedAt should be null for new URLs")
        );
        verify(urlRepository, times(1)).save(shortenedUrl);
    }

    @Test
    void shortenUrl_shouldThrowExceptionForNullOriginalUrl() {
        // Arrange
        String originalUrl = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlService.shortenUrl(originalUrl, testUser), "Expected an IllegalArgumentException for null original URL"
        );
        assertEquals("Original URL cannot be null", exception.getMessage());
    }

    @Test
    void shortenUrl_shouldThrowExceptionForNullUser() {
        // Arrange
        String originalUrl = "https://www.example.com";
        User user = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlService.shortenUrl(originalUrl, user),
                "Expected an IllegalArgumentException for null user"
        );
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void getOriginalUrl_shouldReturnUrlIfExists() {
        // Arrange
        String shortUrlCode = "https://www.test.com";
        Url existingUrl = createUrl("https://www.test.com", LocalDateTime.now());
        when(urlRepository.findByShortUrl(shortUrlCode)).thenReturn(Optional.of(existingUrl));

        // Act
        Optional<Url> originalUrlOpt = urlService.getOriginalUrl(shortUrlCode);

        // Assert
        assertTrue(originalUrlOpt.isPresent(), "Original URL should be present");
        assertEquals(existingUrl, originalUrlOpt.get(), "Returned URL should match the existing URL");
    }

    @Test
    void getOriginalUrl_shouldReturnEmptyIfNotFound() {
        // Arrange
        String shortUrlCode = "https://www.test.com";
        when(urlRepository.findByShortUrl(shortUrlCode)).thenReturn(Optional.empty());

        // Act
        Optional<Url> originalUrlOpt = urlService.getOriginalUrl(shortUrlCode);

        // Assert
        assertFalse(originalUrlOpt.isPresent(), "Original URL should not be present for non-existent short URL");
    }

    @Test
    public void getOriginalUrl_shouldReturnOriginalUrl(){
        String originalUrl="https://www.test.com";
        String shortUrl="kdgfdgj";
        Url expectedUrl=Url.builder()
                        .originalUrl(originalUrl)
                                .shortUrl(shortUrl).build();
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(expectedUrl));
     Optional<Url> result=urlService.getOriginalUrl(shortUrl);
     assertTrue(result.isPresent());
     assertEquals(originalUrl,result.get().getOriginalUrl());
     verify(urlRepository,times(1)).findByShortUrl(anyString());

    }

    @Test
    public void getOriginalUrl_shouldNotReturnUrlIfNotFound(){
        String shortUrl="kdgfdgj";
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());
        Optional<Url> result=urlService.getOriginalUrl(shortUrl);

        assertTrue(result.isEmpty());
        verify(urlRepository,times(1)).findByShortUrl(anyString());

    }
}
