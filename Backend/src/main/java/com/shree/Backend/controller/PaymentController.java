package com.shree.Backend.controller;

import com.razorpay.RazorpayException;
import com.shree.Backend.documents.Payment;
import com.shree.Backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.shree.Backend.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(CREATE_ORDER)
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> request,
                                         Authentication authentication) throws RazorpayException {
        String planType = request.get("planType");
        if(!"premium".equalsIgnoreCase(planType)){
            return ResponseEntity.badRequest().body(Map.of("message","invalid plan type"));
        }
        Payment payment = paymentService.createOrder(authentication.getPrincipal(),planType);
        Map<String, Object> response = Map.of("orderId",payment.getRazorpayOrderId(),
        "amount",payment.getAmount(),"currency",payment.getCurrency(),"receipt",payment.getReceipt() );
;        return ResponseEntity.ok(response);
    }

    @PostMapping(VERIFY)
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request){
        return ResponseEntity.ok(request);
    }

    @GetMapping(HISTORY)
    public ResponseEntity<?> getPaymentHistory(Authentication authentication){
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(GET_ORDER)
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
        return ResponseEntity.ok(orderId);
    }
}
