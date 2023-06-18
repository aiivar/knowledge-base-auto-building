package ru.itis.aivar.kernel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.aivar.kernel.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySentToProcessIsFalseAndQuestionCandidateIsTrue();

    List<Message> findTop5ByQuestionCandidateIsFalseAndDateTimeAfterAndDateTimeBeforeOrderByDateTimeAsc(LocalDateTime after, LocalDateTime before);

}
