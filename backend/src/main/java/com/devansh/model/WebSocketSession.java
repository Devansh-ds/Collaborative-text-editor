package com.devansh.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketSession {
    private String displayName;
    private String docId;
}
