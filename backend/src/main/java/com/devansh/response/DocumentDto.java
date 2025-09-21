package com.devansh.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDto {
    private Long id;
    private String title;
    private String content;
    private String owner;

    private List<UserDocDto> sharedWith;
}
