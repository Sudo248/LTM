package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;

    @Column(name = "content_type")
    private Enum contentType;

    @Column(name = "sender_id")
    private int senderId;

    @Column(name = "sent_at")
    private LocalDate sentAt;

}
