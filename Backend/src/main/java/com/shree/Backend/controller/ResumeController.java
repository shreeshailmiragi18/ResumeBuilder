package com.shree.Backend.controller;

import com.shree.Backend.documents.Resume;
import com.shree.Backend.dto.CreateResumeRequest;
import com.shree.Backend.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.shree.Backend.util.AppConstants.ID;
import static com.shree.Backend.util.AppConstants.UPLOAD_IMAGES;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication) {
        Resume newResume = resumeService.createResume(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }

    @GetMapping
    public ResponseEntity<?> getUserResume(){

    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id){

    }

    @PutMapping(ID)
    public ResponseEntity<?> updateResume(@PathVariable String id, @RequestBody Resume updateData){

    }

    @PutMapping(UPLOAD_IMAGES)
    public ResponseEntity<?> uploadResumeImages(@PathVariable String id, @RequestPart(value = "thumbnail",required = true) MultipartFile thumbnail, @RequestPart(value = "profileImage", required = false)MultipartFile profileImage, HttpServletRequest request){

    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteResume(@PathVariable String id){

    }
}
