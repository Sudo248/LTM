package com.sudo248.ltm.api.model.auth;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 09:53 - 23/10/2022
 */
public class Account implements Serializable {

    private static final long serialVersionUID = -2541856992654826281L;

    private Long id;
    private String password;
    private String email;
    private LocalDate createdAt;

    public Account() {
    }

    public Account(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public Account(Long id, String password, String email, LocalDate createdAt) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
