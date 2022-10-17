package com.sudo248.ltm.api.model.entities;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "profile")
public class ProfileEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bio;
    private String name;
    @Lob
    private String image;

    @Column(name = "is_active")
    private String isActive;
}
