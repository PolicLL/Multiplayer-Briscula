package com.example.web.model;

import com.example.web.security.model.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String username;

    private String password;

    private int age;

    private String country;

    private String email;

    private int points = 0;

    private int level = 1;

    private String photoId;

    @Transient
    public boolean isBot = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MatchDetails> matchDetails = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<Tournament> tournaments = new HashSet<>();
}
