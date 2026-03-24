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
import java.util.Objects;

import static com.shree.Backend.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_CONTROLLER) // AUTH_CONTROLLER is a constant with the endpoint /api/auth
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        log.info("inside the AuthController: register() {}",request);
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("inside the AuthController: verifyEmail() {}",token);
        authService.verifyEmail(token);
        return ResponseEntity.ok().body(Map.of("message","email verified"));
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String,String> body){
        log.info("inside the AuthController: resendVerification() {}",body);

        String email = body.get("email");

        if(Objects.isNull(email)){
            return ResponseEntity.badRequest().body("email is null, email is required");
        }

        authService.resendVerification(email);

        return ResponseEntity.ok().body(Map.of("success", true, "message","verification email sent"));

    }
}
