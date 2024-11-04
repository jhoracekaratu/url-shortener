package com.horace.url_shortener.controller;

import com.horace.url_shortener.dto.UserRegistrationRequest;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated // Enable validation for request parameters
@Tag(name = "Users", description = "API for user management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user", description = "Registers a new user with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody UserRegistrationRequest registrationRequest) {

        System.out.println(registrationRequest);
        User user = userService.registerUser(registrationRequest.getEmail(), registrationRequest.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(user); // Return 201 Created
    }
}
