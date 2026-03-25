package com.shree.Backend.service;

import com.shree.Backend.documents.User;
import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.dto.LoginRequest;
import com.shree.Backend.dto.RegisterRequest;
import com.shree.Backend.exceptions.ResourceExistsException;
import com.shree.Backend.repository.UserRepository;
import com.shree.Backend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;


    @Value("${app.base,url:http://localhost:8080}")
    private String appUrl;

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService: register() {}",request);// displays the logs which makes the debug easy

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("User already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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
            String html =
                    "<div style='font-family:Arial, sans-serif; background:#f4f6fb; padding:30px;'>" +

                            "<div style='max-width:500px; margin:auto; background:#ffffff; border-radius:12px; " +
                            "box-shadow:0 4px 20px rgba(0,0,0,0.08); padding:30px; text-align:center;'>" +

                            "<h2 style='color:#333; margin-bottom:10px;'>Verify Your Email</h2>" +

                            "<p style='color:#555; font-size:15px;'>Hi <b>" + user.getName() + "</b>,</p>" +
                            "<p style='color:#666; font-size:14px; line-height:1.6;'>Thanks for signing up! " +
                            "Please confirm your email address to activate your account.</p>" +

                            "<a href='" + link + "' " +
                            "style='display:inline-block; margin-top:20px; padding:12px 20px; " +
                            "background:linear-gradient(135deg,#6366f1,#4f46e5); color:#ffffff; " +
                            "text-decoration:none; font-weight:bold; border-radius:8px; font-size:14px;'>"
                            + "Verify Email</a>" +

                            "<p style='margin-top:25px; font-size:13px; color:#888;'>Or copy and paste this link:</p>" +

                            "<p style='word-break:break-all; font-size:12px; color:#4f46e5;'>" + link + "</p>" +

                            "<hr style='margin:25px 0; border:none; border-top:1px solid #eee;'/>" +

                            "<p style='font-size:12px; color:#999;'>This link will expire in <b>24 hours</b>.</p>" +

                            "<p style='font-size:11px; color:#bbb;'>If you didn’t request this, you can safely ignore this email.</p>" +

                            "</div>" +
                            "</div>";;
            emailService.sendHtmlEmail(user.getEmail(),"verify your email ", html );

        }catch (Exception e){
            throw new RuntimeException("Failed to send verification email: "+e.getMessage());
        }
    }

    public void verifyEmail(String token){
       User user =  userRepository.findByVerificationToken(token).orElseThrow(()-> new RuntimeException("Invalid or expired verification token"));

       if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
           throw new RuntimeException("expired verification token");
       }

       user.setEmailVerified(true);
       user.setVerificationToken(null);
       user.setVerificationExpires(null);
       userRepository.save(user);
    }

    public void resendVerification(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("email already verified");
        }

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        sendVerificationEmail(user);
    }

    public AuthResponse login(LoginRequest request){
        User existingUser = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Invalid email"));

        if(!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())){
            throw new UsernameNotFoundException("Invalid email or password");
        }

        if(!existingUser.isEmailVerified()){
            throw new RuntimeException(existingUser.getEmail() + " is not verified   please verify the email");
        }

        String token = jwtUtil.generateToken(existingUser.getId());

        AuthResponse response = AuthResponse.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .profileImageUrl(existingUser.getProfileImageUrl())
                .subscriptionPlan(existingUser.getSubscriptionPlan())
                .emailVerified(existingUser.isEmailVerified())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(existingUser.getUpdatedAt())
                .build();

        response.setToken(token);
        return response;
    }
}
