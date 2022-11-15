package com.sudo248.ltm.api.model.entities;

import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.conversation.ConversationType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "conversation")
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private ConversationType type;
    private String avtUrl;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ConversationEntity() {

    }

    public Conversation toConversation(String description) {
        return new Conversation(
                id,
                name,
                avtUrl,
                description,
                type,
                createdAt
        );
    }
}


