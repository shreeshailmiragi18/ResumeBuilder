package com.shree.Backend.repository;

import com.shree.Backend.documents.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment,String> {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Payment> findByStatus(String status);
}
