package com.devansh.security;

import com.devansh.exception.OtpException;
import com.devansh.exception.TokenInvalidException;
import com.devansh.exception.UserAlreadyExistException;
import com.devansh.exception.UserException;
import com.devansh.model.Role;
import com.devansh.request.auth.AuthenticationRequest;
import com.devansh.request.auth.OtpVerificationRequest;
import com.devansh.request.auth.RegisterRequest;
import com.devansh.request.auth.ResetPasswordRequest;
import com.devansh.response.AuthenticationResponse;
import com.devansh.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) throws UserAlreadyExistException {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(
            @RequestBody AuthenticationRequest request) throws UserException {
        return authenticationService.authenticate(request);
    }

    @PutMapping("/enable-double-auth")
    public ResponseEntity enableDoubleAuth(@RequestBody AuthenticationRequest request) throws UserException {
        return authenticationService.enableTwoFactAuth(request);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestHeader("Authorization") String refreshToken)
            throws TokenInvalidException {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity otpVerificationHandler(@RequestBody OtpVerificationRequest request) throws ExecutionException, OtpException {
        return authenticationService.verifyOtp(request);
    }

    @PutMapping("/reset-password")
    public ResponseEntity resetPasswordHandler(@RequestBody ResetPasswordRequest request) throws UserException {
        return authenticationService.resetPassword(request);
    }




}

