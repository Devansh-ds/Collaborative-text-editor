package com.devansh.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursorDto {
    private String username;
    private int index;
    private int length;
}
