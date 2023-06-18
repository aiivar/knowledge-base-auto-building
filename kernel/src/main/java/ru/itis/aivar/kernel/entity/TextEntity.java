package ru.itis.aivar.kernel.entity;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TextEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT", length = 2048)
    protected String text;

    protected Integer label;

}
