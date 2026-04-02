package com.shree.Backend.service;

import com.shree.Backend.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final AuthService authService;

    public Map<String,Object> getTemplate(Object principal ) {

        AuthResponse authResponse = authService.getProfile(principal);


        List<String> availableTemplates;

        Boolean isPremium = "premium".equalsIgnoreCase(authResponse.getSubscriptionPlan());
        if(isPremium){
            availableTemplates = List.of("01", "02", "03");
        }else{
            availableTemplates = List.of("01");
        }

        Map<String, Object> restrictions = new HashMap<>();
        restrictions.put("availableTemplates", availableTemplates);
        restrictions.put("allTemplates", List.of("01", "02", "03"));
        restrictions.put("isPremium", isPremium);
        restrictions.put("subscriptionPlan", authResponse.getSubscriptionPlan());

        return restrictions;
    }
}
