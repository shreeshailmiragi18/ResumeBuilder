package com.shree.Backend.controller;

import com.shree.Backend.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/send-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> sendResumeByEmail(
            @RequestPart("recipientEmail") String recipientEmail,
            @RequestPart("subject") String subject,
            @RequestPart("message") String message,
            @RequestPart("pdfFile") MultipartFile pdfFile,
            Authentication authentication) throws IOException, MessagingException {

        Map<String,Object> response = new HashMap<>();
        if(Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)) {
            response.put("success", false);
            response.put("message","missing required fields");
            return ResponseEntity.badRequest().body(response);
        }

        byte[] pdfBytes = pdfFile.getBytes();
        String originalFileName = pdfFile.getOriginalFilename();
        String filename = Objects.nonNull(originalFileName) ? originalFileName : "resume.pdf";

        String emailSubject = Objects.nonNull(subject)? subject : "Resume Application";
        String emailBody  = Objects.nonNull(message)? message : "please find my resume";

        emailService.sendEmailWithAttachment(recipientEmail,emailSubject,emailBody,pdfBytes,filename);
        response.put("success", true);
        response.put("message","resume sent successfully to "+ recipientEmail);
        return ResponseEntity.ok(response);
    }


}
