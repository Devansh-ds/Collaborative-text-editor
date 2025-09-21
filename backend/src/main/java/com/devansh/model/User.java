package com.devansh.model;

import com.devansh.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    private List<UserDoc> accessDoc;

    @Column(name = "email_verified")
    private boolean isEmailVerified;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }


}

























