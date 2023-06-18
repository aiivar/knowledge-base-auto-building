package ru.itis.aivar.kernel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionEntity extends TextEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_entity_id")
    private QAEntity parent;
}
