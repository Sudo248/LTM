package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "user_conversation")
public class UserConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "conversation_is")
    private int conversationId;

    @Column(name = "joined_at")
    private LocalDate joinedAt;
}
