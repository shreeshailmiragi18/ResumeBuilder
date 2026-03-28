package com.shree.Backend.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "resume")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resume {

    @Id
    @JsonProperty("_id")
    private String id;

    private String userId;

    private String title;

    private String thumbnailLink;

    private Template template;

    private ProfileInfo profileInfo;

    private ContactInfo contactInfo;

    private List<WorkExperienceInfo> workExperienceInfo;

    private List<EducationInfo> educationInfo;

    private List<SkillsInfo> skillsInfo;

    private List<ProjectInfo> projectInfo;

    private List<CertificationInfo> certificateInfo;

    private List<LanguageInfo> languageInfo;

    private List<String> interests;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Template{
        private String theme;
        private List<String> colorPalette;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileInfo{
        private String profilePreviewUrl;
        private String fullName;
        private String designation;
        private String summary;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactInfo{
        private String email;
        private String phone;
        private String location;
        private String linkedIn;
        private String github;
        private String website;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkExperienceInfo{
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private String description;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EducationInfo{
        private String degree;
        private String institution;
        private String startDate;
        private String endDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillsInfo{
        private String name;
        private String progress;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectInfo{
        private String title;
        private String description;
        private String githubLink;
        private String liveDemo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CertificationInfo{
        private String title;
        private String issuer;
        private String year;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LanguageInfo{
        private String name;
        private String progress;
    }

}
