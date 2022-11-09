package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "contact")
public class ContactEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "contact_type")
    private ContactType contactType;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "friend_id")
    private Integer friendId;

    public ContactEntity() {
    }

    public ContactEntity(ContactType contactType, Integer userId, Integer friendId) {
        this.contactType = contactType;
        this.userId = userId;
        this.friendId = friendId;
    }
}

