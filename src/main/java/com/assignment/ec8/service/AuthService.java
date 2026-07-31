package com.assignment.ec8.service;

import com.assignment.ec8.dto.request.LoginRequest;
import com.assignment.ec8.dto.request.RegisterRequest;
import com.assignment.ec8.dto.response.AuthResponse;
import com.assignment.ec8.entity.Role;
import com.assignment.ec8.entity.User;
import com.assignment.ec8.exception.BadRequestException;
import com.assignment.ec8.exception.UnauthorizedException;
import com.assignment.ec8.repository.UserRepository;
import com.assignment.ec8.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' sudah terdaftar");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' sudah terdaftar");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        return toResponse(saved, null);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Username atau password salah"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Username atau password salah");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return toResponse(user, token);
    }

    private AuthResponse toResponse(User user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .tokenType(token != null ? "Bearer" : null)
                .build();
    }
}
