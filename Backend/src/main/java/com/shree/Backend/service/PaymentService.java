package com.shree.Backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shree.Backend.documents.Payment;
import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuthService authService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
;
    public Payment createOrder(Object principal, String planType) throws RazorpayException {
        AuthResponse authResponse = authService.getProfile(principal);
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId,razorpayKeySecret);

        int amount = 99900; //in paise
        String currency = "INR";
        String receipt = "premium_"+ UUID.randomUUID().toString().substring(0,8);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",amount);
        orderRequest.put("currency",currency);
        orderRequest.put("receipt",receipt);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        Payment newPayment = Payment.builder()
                .userId(authResponse.getId())
                .razorpayOrderId(razorpayOrder.get("id"))
                .amount(amount)
                .currency(currency)
                .planType(planType)
                .status("created")
                .receipt(receipt)
                .build();

        return paymentRepository.save(newPayment);


    }
}
