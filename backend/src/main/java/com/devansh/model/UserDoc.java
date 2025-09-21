package com.devansh.model;

import com.devansh.model.enums.Permission;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UserDoc {

    @EmbeddedId
    private UserDocId userDocId;

    @MapsId("username")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @MapsId("docId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docId", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Doc doc;

    private Permission permission;
}
