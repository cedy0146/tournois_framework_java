package com.tpe.tournoi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users",
        uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    /** Mot de passe haché (BCrypt). */
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 100)
    private String nomComplet;

    @Column(nullable = false)
    private boolean actif;

    /** Rôles séparés par des virgules : ADMIN, MANAGER, OPERATOR, USER. */
    @Column(nullable = false, length = 200)
    private String roles;

    public AppUser() {
    }

    public AppUser(Long id, String username, String password, String nomComplet,
                   boolean actif, String roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nomComplet = nomComplet;
        this.actif = actif;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String nomComplet;
        private boolean actif;
        private String roles;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder nomComplet(String nomComplet) {
            this.nomComplet = nomComplet;
            return this;
        }

        public Builder actif(boolean actif) {
            this.actif = actif;
            return this;
        }

        public Builder roles(String roles) {
            this.roles = roles;
            return this;
        }

        public AppUser build() {
            return new AppUser(id, username, password, nomComplet, actif, roles);
        }
    }
}
