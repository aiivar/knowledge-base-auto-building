package ru.itis.aivar.kernel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.aivar.kernel.entity.QAEntity;

public interface QAEntityRepository extends JpaRepository<QAEntity, Long> {
}
