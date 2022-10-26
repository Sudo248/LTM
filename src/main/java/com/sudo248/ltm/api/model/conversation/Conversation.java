package com.sudo248.ltm.api.model.conversation;

import java.io.Serializable;
import java.time.LocalDate;

public class Conversation implements Serializable {

    private static final long serialVersionUID = 4868618689017493245L;

    private Integer id;
    private String name;
    private String avtUrl;
    private String description = "";
    private ConversationType type;
    private LocalDate createAt;

    public Conversation() {
    }

    public Conversation(Integer id, String name, String avtUrl, String description, ConversationType type, LocalDate createAt) {
        this.id = id;
        this.name = name;
        this.avtUrl = avtUrl;
        this.description = description;
        this.type = type;
        this.createAt = createAt;
    }

    public Conversation(Integer id, String name, String avtUrl, ConversationType type, LocalDate createAt) {
        this.id = id;
        this.name = name;
        this.avtUrl = avtUrl;
        this.type = type;
        this.createAt = createAt;
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

    public String getAvtUrl() {
        return avtUrl;
    }

    public void setAvtUrl(String avtUrl) {
        this.avtUrl = avtUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }
}
