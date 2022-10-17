package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "contact")
public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Enum status;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "friend_id")
    private int friendId;
}
