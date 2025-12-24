package com.hasalp.ctoulel_user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Roleid;

    @Column(unique = true, nullable = false)
    private String name; // ROLE_USER, ROLE_ADMIN
}