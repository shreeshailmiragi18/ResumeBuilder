package com.shree.Backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shree.Backend.documents.Resume;
import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String,String> uploadSingleImage(MultipartFile file) throws IOException {
       Map<String,Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type","image"));
       return Map.of("imageUrl",imageUploadResult.get("secure_url").toString());

    }

    public Map<String, String> uploadResumeImage(String id, @Nullable Object principal, MultipartFile thumbnail, MultipartFile profileImage) throws IOException {
        AuthResponse response = authService.getProfile(principal);
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(),id)
                .orElseThrow(()-> new RuntimeException("resume not found"));

        Map<String, String> returnValue = new HashMap<>();
        Map<String, String> uploadResult;
        if(Objects.nonNull(thumbnail)){
            uploadResult = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            returnValue.put("thumbnailLink",uploadResult.get("imageUrl"));
        }

        if(Objects.nonNull(profileImage)){
            uploadResult = uploadSingleImage(profileImage);
            if (Objects.isNull(existingResume.getProfileInfo())) {
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("imageUrl"));
            returnValue.put("profilePreviewUrl",uploadResult.get("imageUrl"));
        }

        resumeRepository.save(existingResume);
        returnValue.put("message", "Images uploaded successfully");
        return returnValue;

    }
}
