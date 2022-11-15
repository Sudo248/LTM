package com.sudo248.ltm.api.model.conversation;

/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:18 - 25/10/2022
 */

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Conversation implements Serializable {

    private static final long serialVersionUID = 4868618689017493245L;

    private Integer id;
    private String name;
    private String avtUrl;
    private String description = "";
    private ConversationType type;
    private LocalDateTime createAt;

    public Conversation() {
    }

    public Conversation(Integer id, String name, String avtUrl, String description, ConversationType type, LocalDateTime createAt) {
        this.id = id;
        this.name = name;
        this.avtUrl = avtUrl;
        this.description = description;
        this.type = type;
        this.createAt = createAt;
    }

    public Conversation(Integer id, String name, String avtUrl, ConversationType type, LocalDateTime createAt) {
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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
