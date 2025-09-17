package com.example.Expense.Management.backend.controller;

import com.example.Expense.Management.backend.dto.RegisterRequest;
import com.example.Expense.Management.backend.model.Role;
import com.example.Expense.Management.backend.model.User;
import com.example.Expense.Management.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        String name = request.getName();
        String email = request.getEmail();
        String password = request.getPassword();

        Set<Role> roles = new HashSet<>();
        Set<String> rolesStr = request.getRoles();
        if (rolesStr != null && !rolesStr.isEmpty()) {
            for (String roleStr : rolesStr) {
                if ("ADMIN".equalsIgnoreCase(roleStr)) {
                    roles.add(Role.ADMIN);
                } else if ("USER".equalsIgnoreCase(roleStr)) {
                    roles.add(Role.USER);
                }
            }
        } else {
            // Default to USER role if no roles specified
            roles.add(Role.USER);
        }

        User user = authService.registerUser(name, email, password, roles);
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "roles", user.getRoles()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Map<String, Object> authResult = authService.loginUser(email, password);
        return ResponseEntity.ok(authResult);
    }
}
