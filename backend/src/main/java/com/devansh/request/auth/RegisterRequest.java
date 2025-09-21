package com.devansh.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @JsonProperty(required = true)
    private String username;
    @JsonProperty(required = true)
    private String email;
    @JsonProperty(required = true)
    private String password;
}
