package com.devansh.two_factor_auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "two.factor.auth")
public class OneTimePasswordConfigurationProperties {
    private OTP otp = new OTP();

    @Data
    public class OTP {
        private Integer expirationMinutes;
    }
}
