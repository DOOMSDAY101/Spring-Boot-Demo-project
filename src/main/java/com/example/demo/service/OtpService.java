package com.example.demo.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Otp;
import com.example.demo.repository.OtpRepository;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_MINUTES = 5;

    private OtpRepository otpRepository;
    private JWTService actionTokenService;

    @Autowired
    public OtpService(OtpRepository otpRepository, JWTService actionTokenService) {
        this.otpRepository = otpRepository;
        this.actionTokenService = actionTokenService;
    }

    private final SecureRandom random = new SecureRandom();

    public String generateOtp(String username) {

        Otp existingOtp = otpRepository.findByUsername(username).orElse(null);

        if (existingOtp != null) {

            // Remove expired OTP
            if (existingOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
                otpRepository.delete(existingOtp);
            } else {
                // Still valid, return the same OTP
                return existingOtp.getCode();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        String code = sb.toString();

        Otp otp = Otp.builder()
                .username(username)
                .code(code)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES))
                .build();
        otpRepository.save(otp);

        return code; // for now, returned directly instead of emailed/texted
    }

    public String verifyOtpAndIssueActionToken(String username, String code) {
        Otp otp = otpRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new IllegalArgumentException("OTP has expired");
        }

        if (!otp.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        // OTP is valid - consume it
        otpRepository.delete(otp);

        return actionTokenService.generateActionToken(username);
    }
}