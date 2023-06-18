package ru.itis.aivar.kernel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageToReceive {

    private String chatId;
    private String text;
    private LocalDateTime dateTime;

}
