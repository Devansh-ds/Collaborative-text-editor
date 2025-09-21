package com.devansh.security.service;

import com.devansh.request.auth.*;
import com.devansh.security.jwt.JwtService;
import com.devansh.exception.OtpException;
import com.devansh.exception.TokenInvalidException;
import com.devansh.exception.UserAlreadyExistException;
import com.devansh.exception.UserException;
import com.devansh.model.enums.Role;
import com.devansh.model.User;
import com.devansh.repo.UserRepository;
import com.devansh.response.AuthenticationResponse;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoadingCache oneTimePasswordCache;
    private final EmailService emailService;


    public AuthenticationResponse register(RegisterRequest request) throws UserAlreadyExistException {

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .isEmailVerified(false)
                .build();

        if (userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email or Username already in use: " + request.getEmail() + ", " + request.getUsername());
        }

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
    }

    private Map getOtpSendMessage() {
        final var response = new HashMap<>();
        response.put("message", "OTP sent successfully sent to your registered email-address. verify it using /verify-otp endpoint");
        return response;
    }

    private void sendOtp(User savedUser, String subject) {
        final var otp = new Random().ints(1, 100000, 999999).sum();

        // cache user details + otp
        Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        data.put("username", savedUser.getUsername());
        data.put("email", savedUser.getEmail());
        data.put("role", savedUser.getRole());
        data.put("password", savedUser.getPassword());

        oneTimePasswordCache.put(savedUser.getEmail(), data);

        System.out.println("OtpException sent :: " + otp);

        CompletableFuture.supplyAsync(() -> {
            emailService.sendEmail(savedUser.getEmail(), subject, "OTP: " + otp);
            return HttpStatus.OK;
        }).thenAccept(status -> {
            System.out.println("Email sent with status: " + status);
        });
    }

    public ResponseEntity verifyOtp(final OtpVerificationRequest otpVerificationRequestDto) throws ExecutionException, OtpException {
        Object cached = oneTimePasswordCache.get(otpVerificationRequestDto.getEmailId());
        if (cached instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) cached;
            int otp = (Integer) data.get("otp");

            if (otp != otpVerificationRequestDto.getOneTimePassword()) {
                System.out.println("Invalid otp: " + otp);
                throw new OtpException("Invalid OTP");
            }

            if (otpVerificationRequestDto.getContext().equals(OtpContext.SIGN_UP)) {
                User user = User.builder()
                        .username((String) data.get("username"))
                        .email((String) data.get("email"))
                        .password((String) data.get("password"))
                        .role((Role) data.get("role"))
                        .createdAt(LocalDateTime.now())
                        .isEmailVerified(true)
                        .build();
                user = userRepository.save(user);

                // invalidating OTP
                oneTimePasswordCache.invalidate(user.getEmail());

                String accessToken = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);

                return ResponseEntity
                        .ok(AuthenticationResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build());

            } else if (otpVerificationRequestDto.getContext().equals(OtpContext.LOGIN)) {
                User loginUser = userRepository
                        .findByEmail(otpVerificationRequestDto.getEmailId())
                        .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + otpVerificationRequestDto.getEmailId()));

                String accessToken = jwtService.generateToken(loginUser);
                String refreshToken = jwtService.generateRefreshToken(loginUser);

                return ResponseEntity
                        .ok(AuthenticationResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build());

            } else if (otpVerificationRequestDto.getContext().equals(OtpContext.RESET_PASSWORD)) {
                User user = userRepository
                        .findByEmail(otpVerificationRequestDto.getEmailId())
                        .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + otpVerificationRequestDto.getEmailId() ));

                user.setPassword(passwordEncoder.encode(otpVerificationRequestDto.getNewPassword()));
                userRepository.save(user);
                return ResponseEntity.ok("Password resets successfully");
            } else if (otpVerificationRequestDto.getContext().equals(OtpContext.ENABLE_TWO_FACT_AUTH)) {
                User user = userRepository
                        .findByEmail(otpVerificationRequestDto.getEmailId())
                        .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + otpVerificationRequestDto.getEmailId()));
                user.setEmailVerified(!user.isEmailVerified());
                userRepository.save(user);
                return ResponseEntity.ok("Two factor auth changed successfully");

            } else {
                throw new OtpException("OTP CONTEXT not supported");
            }
        } else {
            throw new OtpException(otpVerificationRequestDto.getEmailId() + " does not have a valid otp");
        }
    }

    public ResponseEntity authenticate(AuthenticationRequest request) throws UserException {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(request.getEmail() + " does not exist"));

        if (user.isEmailVerified()) {
            sendOtp(user, "2FA: Request to log in to your account");
            return ResponseEntity.ok(getOtpSendMessage());
        } else {
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());
        }
    }

    public AuthenticationResponse refreshToken(String refreshToken) throws TokenInvalidException {

        String userEmail;

        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new TokenInvalidException("token might be null or empty");
        }

        refreshToken = refreshToken.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException(userEmail));

            if (jwtService.validateToken(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                return new AuthenticationResponse(accessToken, refreshToken);
            } else {
                throw new TokenInvalidException("refresh token is invalid");
            }
        } else {
            throw new TokenInvalidException("user email does not exist");
        }

    }

    public ResponseEntity resetPassword(ResetPasswordRequest request) throws UserException {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException("User does not exist with email: " + request.email()));

        sendOtp(user, "Reset Password");
        return ResponseEntity.ok(getOtpSendMessage());
    }

    public ResponseEntity enableTwoFactAuth(AuthenticationRequest request) throws UserException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(request.getEmail() + " does not exist"));

        sendOtp(user, "2FA: Enable Two factor authentication");
        return ResponseEntity.ok(getOtpSendMessage());

    }
}













