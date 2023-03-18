package com.horace.url_shortener.integration;

import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); // Clear the database before each test
    }

    @Test
    public void registerUser_shouldCreateUser() throws Exception {
        String requestBody = """
            {
                "email": "test@test.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    public void registerUser_shouldReturnConflictForExistingUser() throws Exception {
        String requestBody = """
            {
                "email": "test@test.com",
                "password": "password"
            }
            """;

        // Register the user first
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Try to register the same user again
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Email already in use"));
    }

    @Test
    public void registerUser_shouldReturnBadRequestForEmptyEmail() throws Exception {
        String requestBody = """
            {
                "email": "",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Email cant be blank"));
    }

//    @Test
//    public void registerUser_shouldReturnBadRequestForEmptyPassword() throws Exception {
//        String requestBody = """
//            {
//                "email": "test@test.com",
//                "password": ""
//            }
//            """;
//
//        mockMvc.perform(post("/api/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").value("Password cant be blank"));
//    }

    @Test
    public void registerUser_shouldReturnBadRequestForInvalidEmailFormat() throws Exception {
        String requestBody = """
            {
                "email": "invalid-email",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format"));
    }


    @Test
    public void registerUser_shouldReturnErrorForShortPassword() throws Exception {
        String requestBody = """
            {
                "email": "test@test.com",
                "password": "123"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password too short"));
    }
}
