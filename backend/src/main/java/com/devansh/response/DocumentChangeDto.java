package com.devansh.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentChangeDto {

    private String id;
    private String left;
    private String right;
    private String content;
    private String operation;

    @JsonProperty("isDeleted")
    private boolean isDeleted;
    @JsonProperty("isBold")
    private boolean isBold;
    @JsonProperty("isItalic")
    private boolean isItalic;

}
