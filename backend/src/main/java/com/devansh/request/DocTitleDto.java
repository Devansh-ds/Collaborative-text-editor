package com.devansh.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocTitleDto {

    @NotNull(message = "Title is required")
    @JsonProperty(required = true)
    private String title;
}
