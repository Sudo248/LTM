package com.sudo248.ltm.api.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "user_conversation")
public class UserConversationEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "conversation_id")
    private Integer conversationId;

    @Column(name = "joined_at")
    private LocalDate joinedAt;

    public UserConversationEntity() {
    }

    public UserConversationEntity(Integer userId, Integer conversationId, LocalDate joinedAt) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.joinedAt = joinedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public LocalDate getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDate joinedAt) {
        this.joinedAt = joinedAt;
    }
}
