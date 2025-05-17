package com.fesa.sharetools.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "USERS")
public class User {

    // Getters e Setters
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @Setter
    @Getter
    @Column(name = "NAME")
    private String name;

    @Setter
    @Getter
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Setter
    @Getter
    @Column(name = "PHONE")
    private String phone;

    @Setter
    @Getter
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Tool> tools;


}
