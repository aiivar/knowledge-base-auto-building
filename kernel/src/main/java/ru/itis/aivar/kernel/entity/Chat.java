package ru.itis.aivar.kernel.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chat extends BaseEntity {

    @Column(columnDefinition = "TEXT", length = 2048)
    private String name;

    @Column(columnDefinition = "TEXT", length = 2048)
    private String extId;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    public static Chat newInstance(String extId) {
        return Chat.builder()
                .extId(extId)
                .messages(new ArrayList<>())
                .build();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChat(this);
    }
}
