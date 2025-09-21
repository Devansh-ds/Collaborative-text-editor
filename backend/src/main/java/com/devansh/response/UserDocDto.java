package com.devansh.response;

import com.devansh.model.enums.Permission;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDocDto {

    @NotNull(message = "username is required")
    @JsonProperty(required = true)
    private String username;

    @NotNull(message = "Permission is required")
    @JsonProperty(required = true)
    private Permission permission;
}
