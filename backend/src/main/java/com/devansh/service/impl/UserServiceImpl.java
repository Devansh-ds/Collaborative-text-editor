package com.devansh.service.impl;

import com.devansh.security.jwt.JwtService;
import com.devansh.exception.UserException;
import com.devansh.model.User;
import com.devansh.repo.UserRepository;
import com.devansh.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User findByJwtToken(String token) throws UserException {
        token = token.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException("User not found with email: " + email));
        return user;
    }

}































