package com.digitalvault.vault.service;

import com.digitalvault.vault.dto.RegisterRequest;
import com.digitalvault.vault.entity.Role;
import com.digitalvault.vault.entity.User;
import com.digitalvault.vault.repository.RoleRepository;
import com.digitalvault.vault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.digitalvault.vault.dto.LoginRequest;
import com.digitalvault.vault.dto.AuthResponse;
import com.digitalvault.vault.security.JwtUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    //@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12) ;

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already taken!";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered!";
        }

        // Encode the password
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // Create User object
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);

        // Assign ROLE_USER
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        if (userRole.isEmpty()) {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            roleRepository.save(newRole);
            userRole = Optional.of(newRole);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole.get());
        user.setRoles(roles);

        // Save to DB
        userRepository.save(user);
        return "User registered successfully!";
    }

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        String token = jwtUtil.generateToken(user.getUsername(), roles);

        return new AuthResponse(token, user.getUsername(), user.getEmail(), roles);
    }

}
