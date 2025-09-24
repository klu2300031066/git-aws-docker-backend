package com.klu.ecommerce.service;

import com.klu.ecommerce.entity.User;
import com.klu.ecommerce.repository.UserRepository;
import com.klu.ecommerce.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> registerUser(String username, String email, String password) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            response.put("status", "error");
            response.put("message", "User already exists!");
            return response;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "User registered successfully!");
        return response;
    }

    public Map<String, String> loginUser(String username, String password) {
        Map<String, String> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            response.put("status", "error");
            response.put("message", "Invalid username or password");
            return response;
        }

        String token = jwtUtil.generateToken(username);
        response.put("status", "success");
        response.put("token", token);
        return response;
    }
}
