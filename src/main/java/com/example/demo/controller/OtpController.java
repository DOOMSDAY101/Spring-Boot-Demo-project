package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OtpService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/otp")
@SecurityRequirement(name = "bearerAuth")
public class OtpController {

    private OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // Authenticated with the normal JWT (SecurityContext already populated)
    @GetMapping("/generate")
    public ResponseEntity<?> generateOtp(
            Authentication authentication) {
        String username = authentication.getName();
        String otp = otpService.generateOtp(username);
        // TODO: replace with actual email/SMS delivery later; returned directly for now
        return ResponseEntity.ok(otp);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            Authentication authentication,
            @RequestParam String code) {
        String username = authentication.getName();
        try {
            String actionToken = otpService.verifyOtpAndIssueActionToken(username, code);
            return ResponseEntity.ok(actionToken);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}