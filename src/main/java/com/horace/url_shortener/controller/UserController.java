package com.horace.url_shortener.controller;

import com.horace.url_shortener.dto.UserRegistrationRequest;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated // Enable validation for request parameters
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        System.out.println(registrationRequest);
            User user = userService.registerUser(registrationRequest.getEmail(), registrationRequest.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(user); // Return 201 Created

    }
}

