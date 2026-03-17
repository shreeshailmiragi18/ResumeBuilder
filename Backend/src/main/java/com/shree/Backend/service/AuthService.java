package com.shree.Backend.service;

import com.shree.Backend.documents.User;
import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.dto.RegisterRequest;
import com.shree.Backend.exceptions.ResourceExistsException;
import com.shree.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;


    @Value("${app.base,url:http://localhost:8080 }")
    private String appUrl;

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService: register() {}",request);// displays the logs which makes the debug easy

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("User already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusDays(1))
                .build();

        userRepository.save(user);
        sendVerificationEmail(user);

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .subscriptionPlan(user.getSubscriptionPlan())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private void sendVerificationEmail(User user) {
        try{
            String link = appUrl + "/api/auth/verify-email?token="+user.getVerificationToken();
            String html = "<div style='font-family:sans-serif'>" +
                    "<h2>Verify your email</h2>" +
                    "<p>Hi " + user.getName() + ", please confirm your email to activate your account.</p>" +
                    "<p>" +
                    "<a href='" + link + "' " +
                    "style='display:inline-block;padding:10px 16px;background:#6366f1;color:#fff;border-radius:6px;text-decoration:none;'>"
                    + "Verify Email</a>" +
                    "</p>" +
                    "<p>Or copy this link:" + link + "</p>" +
                    "<p>This link expires in 24 hours.</p>" +
                    "</div>";
            emailService.sendHtmlEmail(user.getEmail(),"verify your email ", html );

        }catch (Exception e){
            throw new RuntimeException("Failed to send verification email: "+e.getMessage());
        }
    }
}
