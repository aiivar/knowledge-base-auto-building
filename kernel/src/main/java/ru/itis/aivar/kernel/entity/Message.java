package ru.itis.aivar.kernel.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.itis.aivar.kernel.dto.MessageToReceive;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message extends BaseEntity {

    @Column(columnDefinition = "TEXT", length = 2048)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_id")
    private Chat chat;

    private Boolean processed;

    private Boolean sentToProcess;

    private Boolean questionCandidate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime dateTime;

    public static Message newInstance(String text, LocalDateTime dateTime) {
        return Message.builder()
                .text(text)
                .dateTime(dateTime)
                .processed(Boolean.FALSE)
                .sentToProcess(Boolean.FALSE)
                .build();
    }

    public static Message newInstance(MessageToReceive message) {
        return newInstance(message.getText(), message.getDateTime());
    }
}
