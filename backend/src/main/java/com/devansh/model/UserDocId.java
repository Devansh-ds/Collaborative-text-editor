package com.devansh.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class UserDocId {

    private Long docId;
    private String username;

}
