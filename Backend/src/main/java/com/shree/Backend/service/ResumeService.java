package com.shree.Backend.service;


import com.shree.Backend.documents.Resume;
import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.dto.CreateResumeRequest;
import com.shree.Backend.repository.ResumeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuthService authService;

    public Resume createResume(@Valid CreateResumeRequest request, Object principleObject) {
        Resume newResume = new Resume();
        AuthResponse response = authService.getProfile(principleObject);

        newResume.setUserId(response.getId());
        newResume.setTitle(request.getTitle());

        setDefaultResumeData(newResume);
        return resumeRepository.save(newResume);

    }

    private void setDefaultResumeData(Resume newResume) {
        newResume.setProfileInfo(new Resume.ProfileInfo());
        newResume.setContactInfo(new Resume.ContactInfo());
        newResume.setWorkExperienceInfo(new ArrayList<>());
        newResume.setEducationInfo(new ArrayList<>());
        newResume.setSkillsInfo(new ArrayList<>());
        newResume.setProjectInfo(new ArrayList<>());
        newResume.setCertificateInfo(new ArrayList<>());
        newResume.setLanguageInfo(new ArrayList<>());
        newResume.setInterests(new ArrayList<>());
    }

    public List<Resume> getUserResumes( Object principal) {
        AuthResponse response = authService.getProfile(principal);
        List<Resume> resumes = resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());
        return resumes;
    }

    public Resume getResumeById(String resumeId, Object principal) {
        AuthResponse response = authService.getProfile(principal);
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(()-> new RuntimeException("Resume not found"));
        return existingResume;
    }

    public Resume updateResume(String resumeId, Resume updateData,  Object principal) {
        AuthResponse response = authService.getProfile(principal);
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(()-> new RuntimeException("Resume not found"));
        existingResume.setTitle(updateData.getTitle());
        existingResume.setThumbnailLink(updateData.getThumbnailLink());
        existingResume.setTemplate(updateData.getTemplate());
        existingResume.setProfileInfo(updateData.getProfileInfo());
        existingResume.setContactInfo(updateData.getContactInfo());
        existingResume.setWorkExperienceInfo(updateData.getWorkExperienceInfo());
        existingResume.setEducationInfo(updateData.getEducationInfo());
        existingResume.setSkillsInfo(updateData.getSkillsInfo());
        existingResume.setProjectInfo(updateData.getProjectInfo());
        existingResume.setCertificateInfo(updateData.getCertificateInfo());
        existingResume.setLanguageInfo(updateData.getLanguageInfo());
        existingResume.setInterests(updateData.getInterests());


        resumeRepository.save(existingResume);
        return existingResume;
    }
}
