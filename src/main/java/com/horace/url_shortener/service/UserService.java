package com.horace.url_shortener.service;



import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.exceptions.UserAlreadyExistsException;
import com.horace.url_shortener.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password) {


        if(email.isBlank()){
            throw new IllegalArgumentException("Email cant be blank");
        }
        if(password.isBlank()){
            throw new IllegalArgumentException("Password cant be blank");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

}
