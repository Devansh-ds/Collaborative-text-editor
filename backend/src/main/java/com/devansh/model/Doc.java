package com.devansh.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "docs")
public class Doc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private User owner;

    private String title;

    @Lob
    @Column(name = "content")
    private byte[] content;

    @OneToMany(mappedBy = "doc")
    private List<UserDoc> sharedWith;

}
