package ua.yuriih.carrental.lab2.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "passport_id")
    private final long passportId;

    @Column(unique = true, nullable = false)
    private final String name;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    private final String keycloakId;
}
