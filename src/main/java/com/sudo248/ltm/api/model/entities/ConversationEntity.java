package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity

@Table(name = "conversation")
public class ConversationEntity  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Type type;

    @Column(name = "created_at")
    private LocalDate createdAt;
}

enum Type {
    GROUP,
    P2P
}
