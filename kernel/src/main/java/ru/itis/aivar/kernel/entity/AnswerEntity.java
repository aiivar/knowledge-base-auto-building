package ru.itis.aivar.kernel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AnswerEntity extends TextEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_entity_id")
    private QAEntity parent;
}
