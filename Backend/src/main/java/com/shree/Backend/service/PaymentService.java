package com.shree.Backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shree.Backend.documents.Payment;
import com.shree.Backend.documents.User;
import com.shree.Backend.dto.AuthResponse;
import com.shree.Backend.repository.PaymentRepository;
import com.shree.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

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

    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {
       try{
           JSONObject orderRequest = new JSONObject();
           orderRequest.put("razorpay_order_id", razorpayOrderId);
           orderRequest.put("razorpay_payment_id", razorpayPaymentId);
           orderRequest.put("razorpay_signature", razorpaySignature);

           boolean isValidSignature = Utils.verifyPaymentSignature(orderRequest,razorpaySignature);
           if(isValidSignature){
               Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                       .orElseThrow(()-> new RuntimeException("Payment not found"));
               payment.setRazorpayPaymentId(razorpayPaymentId);
               payment.setRazorpaySignature(razorpaySignature);
               payment.setStatus("paid");
               paymentRepository.save(payment);

               upgradeUserSubscription(payment.getUserId(),payment.getPlanType());
               return true;
           }
           return false;
       }catch (Exception e){
           log.error("verifyPayment error",e);
           return false;

       }

    }

    private void upgradeUserSubscription(String userId, String planType) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("user not found"));
        existingUser.setSubscriptionPlan(planType);
        userRepository.save(existingUser);

    }

    public List<Payment> getUserPayments( Object principal) {
        AuthResponse authResponse = authService.getProfile(principal);

        return paymentRepository.findByUserIdOrderByCreatedAtDesc(authResponse.getId());

    }

    public Payment getPaymentDetails(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(()-> new RuntimeException("Payment not found"));
    }


}
