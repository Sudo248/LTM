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
public class ConversationEntity implements Serializable {

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

    public ConversationEntity(String name, ConversationType type, String avtUrl, LocalDateTime createdAt) {
        this.name = name;
        this.type = type;
        this.avtUrl = avtUrl;
        this.createdAt = createdAt;
    }

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setType(ConversationType type) {
//        this.type = type;
//    }
//
//    public String getAvtUrl() {
//        return avtUrl;
//    }
//
//    public void setAvtUrl(String avtUrl) {
//        this.avtUrl = avtUrl;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public ConversationType getType() {
//        return type;
//    }

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


