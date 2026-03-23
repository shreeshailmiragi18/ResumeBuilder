package com.shree.Backend.controller;

import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.dto.RegisterRequest;
import com.shree.Backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        log.info("inside the AuthController: register() {}",request);
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("inside the AuthController: verifyEmail() {}",token);
        authService.verifyEmail(token);
        return ResponseEntity.ok().body(Map.of("message","email verified"));
    }

}
