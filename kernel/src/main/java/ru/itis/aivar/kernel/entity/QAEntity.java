package ru.itis.aivar.kernel.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;
import ru.itis.aivar.kernel.labeling.model.QALabelingModel;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QAEntity extends BaseEntity {

    @OneToOne(mappedBy = "parent", cascade = CascadeType.ALL)
    private QuestionEntity questionEntity;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<AnswerEntity> answerEntities;

    public static QALabelingModel from(QAEntity qaEntity) {
        List<QALabelingModel.TextEntityModel> answers = qaEntity.getAnswerEntities().stream().map(row -> {
            QALabelingModel.TextEntityModel textEntityModel = new QALabelingModel.TextEntityModel();
            textEntityModel.setLabel(row.getLabel());
            textEntityModel.setText(row.getText());
            return textEntityModel;
        }).collect(Collectors.toList());
        QALabelingModel.TextEntityModel question = new QALabelingModel.TextEntityModel();
        question.setText(qaEntity.getQuestionEntity().getText());
        question.setLabel(qaEntity.getQuestionEntity().getLabel());
        return QALabelingModel.builder()
                .answers(answers)
                .question(question)
                .build();
    }

    public static QAEntity newInstance(QALabelingModel qaLabelingModel) {
        QAEntity qaEntity = QAEntity.builder().build();
        QuestionEntity question = QuestionEntity.builder()
                .parent(qaEntity)
                .build();
        question.setText(qaLabelingModel.getQuestion().getText());
        question.setLabel(qaLabelingModel.getQuestion().getLabel());
        qaEntity.setQuestionEntity(question);

        List<AnswerEntity> answerEntityList = qaLabelingModel.getAnswers().stream().map(row -> {
            AnswerEntity answerEntity = new AnswerEntity();
            answerEntity.setParent(qaEntity);
            answerEntity.setText(row.getText());
            answerEntity.setLabel(row.getLabel());
            return answerEntity;
        }).collect(Collectors.toList());
        qaEntity.setAnswerEntities(answerEntityList);
        return qaEntity;
    }

}
