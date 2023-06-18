package ru.itis.aivar.kernel.labeling.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.aivar.kernel.entity.Message;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QALabelingModel implements Serializable {

    private TextEntityModel question;
    private List<TextEntityModel> answers;

    public static QALabelingModel create(Message questionCandidate, List<Message> answerCandidates) {
        return QALabelingModel.builder()
                .question(TextEntityModel.create(questionCandidate))
                .answers(answerCandidates.stream().map(TextEntityModel::create).collect(Collectors.toList()))
                .build();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TextEntityModel implements Serializable {

        private String text;

        private Integer label;

        private Long messageId;

        public static TextEntityModel create(Message message) {
            return TextEntityModel.builder()
                    .text(message.getText())
                    .label(0)
                    .messageId(message.getId())
                    .build();
        }

        public Long getMessageId() {
            return messageId;
        }

        public void setMessageId(Long messageId) {
            this.messageId = messageId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getLabel() {
            return label;
        }

        public void setLabel(Integer label) {
            this.label = label;
        }
    }
}
