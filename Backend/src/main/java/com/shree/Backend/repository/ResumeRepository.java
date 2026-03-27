package com.shree.Backend.repository;

import com.shree.Backend.documents.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends MongoRepository<Resume,String> {
}
