package com.horace.url_shortener.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class UserRegistrationRequest {
    @Email(message = "Invalid email format")
    private String email;
    @Length(min = 6,message = "Password too short")
    private String password;
}

