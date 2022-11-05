package com.sudo248.ltm.api.model.entities;

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
    private ConversationType type;
    private String avtUrl;
    @Column(name = "created_at")
    private LocalDate createdAt;

    public ConversationEntity() {

    }
    public ConversationEntity(Integer id, String name, ConversationType type, String avtUrl, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.avtUrl = avtUrl;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getAvtUrl() {
        return avtUrl;
    }

    public void setAvtUrl(String avtUrl) {
        this.avtUrl = avtUrl;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public com.sudo248.ltm.api.model.conversation.ConversationType getType() {
        return com.sudo248.ltm.api.model.conversation.ConversationType.valueOf("type");
    }
}

enum ConversationType {
    GROUP,
    P2P
}


