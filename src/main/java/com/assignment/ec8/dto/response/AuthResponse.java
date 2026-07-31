package com.assignment.ec8.dto.response;

import com.assignment.ec8.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String token;
    private String tokenType;
}
