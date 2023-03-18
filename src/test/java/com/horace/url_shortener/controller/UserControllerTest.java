package com.horace.url_shortener.controller;

import com.horace.url_shortener.config.SecurityConfig;
import com.horace.url_shortener.dto.UserRegistrationRequest;
import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.exceptions.UserAlreadyExistsException;
import com.horace.url_shortener.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    public void registerUser_shouldReturnCreatedUser() throws Exception {
        when(userService.registerUser(anyString(), anyString())).thenReturn(testUser);

        String requestBody = """
            {
                "email": "test@test.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ) // Include CSRF token for security
                .andExpect(status().isCreated()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.password").value("encodedPassword"));

        verify(userService, times(1)).registerUser("test@test.com", "password");
    }
//
//    @Test
//    public  void registerUser_shouldReturnIllegalArgumentForEmptyEmail() throws Exception {
//
//        when(userService.registerUser(argThat(String::isEmpty), anyString())).thenThrow(new IllegalArgumentException("Email cant be blank"));
//        String requestData= """
//                {
//                "email":"",
//                "password":"password"
//                }""";
//        mockMvc.perform(post("/api/users/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestData)
//
//        ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").value("Email cant be blank"));
//        verify(userService,times(1)).registerUser(anyString(),anyString());
//    }
//    @Test
//    public  void registerUser_shouldReturnIllegalArgumentForEmptyPassword() throws Exception {
//
//        when(userService.registerUser( anyString(),argThat(String::isEmpty))).thenThrow(new IllegalArgumentException("Password cant be blank"));
//        String requestData= """
//                {
//                "email":"test@test.com",
//                "password":""
//                }""";
//        mockMvc.perform(post("/api/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestData)
//
//                ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").value("Password cant be blank"));
//        verify(userService,times(1)).registerUser(anyString(),anyString());
//    }

    @Test
    public void registerUser_shouldReturnUserAlreadyExistsForExistingUser() throws Exception {
        when(userService.registerUser(anyString(), anyString())).thenThrow(new UserAlreadyExistsException("Email already in use"));

        String requestBody = """
            {
                "email": "test@test.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$").value("Email already in use"));

        verify(userService, times(1)).registerUser("test@test.com", "password");
    }

    @Test
    public void registerUser_shouldReturnInternalServerError() throws Exception {
        when(userService.registerUser(anyString(), anyString())).thenThrow(new RuntimeException("Unexpected error"));

        String requestBody = """
            {
                "email": "test@test.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError()) // Expect HTTP 500 Internal Server Error
                .andExpect(jsonPath("$",containsString("Unexpected error")));

        verify(userService, times(1)).registerUser("test@test.com", "password");
    }
}
