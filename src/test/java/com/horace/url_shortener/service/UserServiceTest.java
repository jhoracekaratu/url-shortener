package com.horace.url_shortener.service;

import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.exceptions.UserAlreadyExistsException;
import com.horace.url_shortener.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;
    String testEmail="test@test.com";
    String testPassword="password";
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    public User createTestUser(){
        return User.builder()
//                .id(1L)
                .email(testEmail)
                .password(testPassword)
                .build();

    }

    @Test
    public void registerUser_shouldThrowExceptionForExistingUser(){


                String exceptionMessage="Email already in use";
        User testUser=createTestUser();
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

               UserAlreadyExistsException exception=assertThrows(UserAlreadyExistsException.class,()->
                    userService.registerUser(testEmail,testPassword),
                    exceptionMessage
                );

                assertEquals(exception.getMessage(),exceptionMessage);

    }
    @Test
    public void registerUser_shouldSaveUser(){
        User testUser=createTestUser();
        User returneduser=createTestUser();
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn("password");
        when(userRepository.save(any(User.class))).thenReturn(testUser); // Stub the save method.

        User savedUser=userService.registerUser(testEmail,testPassword);

        assertEquals(testUser,savedUser);
        verify(userRepository,times(1)).save(any(User.class));

    }



    }