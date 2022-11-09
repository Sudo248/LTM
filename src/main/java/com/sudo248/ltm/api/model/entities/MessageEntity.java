package com.sudo248.ltm.api.model.entities;

import com.sudo248.ltm.api.model.message.ContentMessageType;
import com.sudo248.ltm.api.model.message.Message;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "message")
public class MessageEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;

    @Column(name = "content_type")
    private ContentMessageType contentType;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "conversation_id")
    private Integer conversationId;

    public MessageEntity(){

    }

    public MessageEntity(String content, ContentMessageType contentType, Integer senderId, LocalDateTime sentAt, Integer conversationId) {
        this.content = content;
        this.contentType = contentType;
        this.senderId = senderId;
        this.sentAt = sentAt;
        this.conversationId = conversationId;
    }

    public Message toMessage(String avtUrl) {
        return new Message(
                id,
                content,
                contentType,
                senderId,
                avtUrl,
                conversationId,
                sentAt
        );
    }

    public static MessageEntity fromMessage(Message message) {
        return new MessageEntity(
                message.getContent(),
                message.getContentType(),
                message.getSendId(),
                message.getSendAt(),
                message.getConversationId()
        );
    }
}

