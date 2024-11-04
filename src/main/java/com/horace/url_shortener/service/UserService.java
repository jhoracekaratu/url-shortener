package com.horace.url_shortener.service;



import com.horace.url_shortener.entity.User;
import com.horace.url_shortener.exceptions.UserAlreadyExistsException;
import com.horace.url_shortener.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger =LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password) {


        if(email.isBlank()){
            logger.warn("Attempting to register user with blank email");
            throw new IllegalArgumentException("Email cant be blank");
        }
        if(password.isBlank()){
            logger.warn("Attempting to register user with blank password");

            throw new IllegalArgumentException("Password cant be blank");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Attempting to register an already existing User");

            throw new UserAlreadyExistsException("Email already in use");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        logger.info("User registered successfully with:{} ",user);
        return userRepository.save(user);
    }

}
