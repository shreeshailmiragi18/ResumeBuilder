package com.shree.Backend.service;

import com.shree.Backend.documents.Resume;
import com.shree.Backend.dto.CreateResumeRequest;
import com.shree.Backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private ResumeRepository resumeRepository;

}
