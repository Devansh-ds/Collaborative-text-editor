package com.devansh.security.service;

import com.devansh.two_factor_auth.EmailConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(EmailConfigurationProperties.class)
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailConfigurationProperties emailConfig;

    @Async
    public void sendEmail(String toMail, String subject, String body) {
        System.out.println("Sending email to " + toMail + " with subject " + subject + " and body " + body);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailConfig.getUsername());
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

}