package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "user")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String password;
    private String email;

    @Column(name = "created_at")
    private LocalDate createdAt;

    private boolean status = true;

    public UserEntity() {
    }

    public UserEntity(Integer id, String email, String password, LocalDate createdAt, boolean status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.status = status;
    }
}
