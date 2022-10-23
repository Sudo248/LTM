package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "message")
public class MessageEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;

    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "sent_at")
    private LocalDate sentAt;

}

enum ContentType {
    TEXT,
    VOICE,
    IMAGE,
    VIDEO
}
