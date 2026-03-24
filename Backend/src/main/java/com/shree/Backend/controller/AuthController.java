package com.shree.Backend.controller;

import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.dto.LoginRequest;
import com.shree.Backend.dto.RegisterRequest;
import com.shree.Backend.service.AuthService;
import com.shree.Backend.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.shree.Backend.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_CONTROLLER) // AUTH_CONTROLLER is a constant with the endpoint /api/auth
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

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

    @PostMapping(FILE_UPLOAD)
    public ResponseEntity<?> uploadImage(@RequestParam("image" )MultipartFile file) throws IOException {
         Map<String,String> response = fileUploadService.uploadSingleImage(file);
         return ResponseEntity.ok().body(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok().body(response);
    }

}
