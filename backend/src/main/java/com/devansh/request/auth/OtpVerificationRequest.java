package com.devansh.request.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpVerificationRequest {
    private String emailId;
    private Integer oneTimePassword;
    private OtpContext context;
    private String newPassword;
}
