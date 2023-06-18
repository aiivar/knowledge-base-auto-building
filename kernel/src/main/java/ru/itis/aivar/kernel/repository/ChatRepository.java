package ru.itis.aivar.kernel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.aivar.kernel.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
