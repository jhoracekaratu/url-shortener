package com.horace.url_shortener.repository;

import com.horace.url_shortener.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    private User testUser;
    String testEmail = "test@test.com";
    String testPassword = "password";
    @BeforeEach
    public void setUp(){

        testUser=User.builder()
                .id(1L)
                        .email(testEmail)
                                .password(testPassword)
                                        .build();
        userRepository.save(testUser);
    }

    @Test
    public void findByEmail_shouldReturnUserIfExists(){
Optional<User> user=userRepository.findByEmail(testEmail);
assertTrue(user.isPresent(), "No user returned");
        String retrievedEmail=user.get().getEmail();
        String retrievedPassword=user.get().getPassword();
assertEquals(retrievedEmail,testEmail,"Email dont match");
assertEquals(retrievedPassword,testPassword,"Password dont match");
    }
    @Test
    public void findByEmail_shouldNotReturnUserIfEmailIsWrong(){
        Optional<User> user=userRepository.findByEmail("another@test.com");
        assertTrue(user.isPresent(), "No user returned");
        String retrievedEmail=user.get().getEmail();
        String retrievedPassword=user.get().getPassword();
        assertEquals(retrievedEmail,testEmail,"Email dont match");
        assertEquals(retrievedPassword,testPassword,"Password dont match");
    }

}