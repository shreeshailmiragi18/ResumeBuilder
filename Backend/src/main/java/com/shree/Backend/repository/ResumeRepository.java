package com.shree.Backend.repository;

import com.shree.Backend.documents.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume,String> {
    List<Resume> findByUserIdOrderByUpdatedAtDesc(String userId);
    Optional<Resume> findByUserIdAndId(String UserId, String id);
}
