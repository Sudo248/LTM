package com.sudo248.ltm.api.model.message;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 07:25 - 26/10/2022
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 3228449910805277786L;

    private Integer id;
    private String content;
    private ContentMessageType contentType;
    private Integer sendId;
    private String avtUrl;
    private LocalDateTime sendAt;

    public Message() {
    }

    public Message(String content, ContentMessageType contentType, Integer sendId, String avtUrl) {
        this.content = content;
        this.contentType = contentType;
        this.sendId = sendId;
        this.avtUrl = avtUrl;
        this.sendAt = LocalDateTime.now();
    }

    public Message(Integer id, String content, ContentMessageType contentType, Integer sendId, String avtUrl) {
        this.id = id;
        this.content = content;
        this.contentType = contentType;
        this.sendId = sendId;
        this.avtUrl = avtUrl;
        this.sendAt = LocalDateTime.now();
    }

    public Message(Integer id, String content, ContentMessageType contentType, Integer sendId, String avtUrl, LocalDateTime sendAt) {
        this.id = id;
        this.content = content;
        this.contentType = contentType;
        this.sendId = sendId;
        this.avtUrl = avtUrl;
        this.sendAt = sendAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentMessageType getContentType() {
        return contentType;
    }

    public void setContentType(ContentMessageType contentType) {
        this.contentType = contentType;
    }

    public Integer getSendId() {
        return sendId;
    }

    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }

    public String getAvtUrl() {
        return avtUrl;
    }

    public void setAvtUrl(String avtUrl) {
        this.avtUrl = avtUrl;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }
}
