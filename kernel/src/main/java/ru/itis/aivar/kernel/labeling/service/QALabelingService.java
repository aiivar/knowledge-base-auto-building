package ru.itis.aivar.kernel.labeling.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.aivar.kernel.entity.Message;
import ru.itis.aivar.kernel.labeling.model.QALabelingModel;
import ru.itis.aivar.kernel.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QALabelingService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.discussions.mark}")
    private String markDiscussionsQueueName;

    public void startLabeling() {
        List<Message> questionCandidates = messageRepository.findAllBySentToProcessIsFalseAndQuestionCandidateIsTrue();
        for (Message questionCandidate : questionCandidates) {
            LocalDateTime answerTimeLimit = questionCandidate.getDateTime().plusDays(1);
            List<Message> answerCandidates = messageRepository.findTop5ByQuestionCandidateIsFalseAndDateTimeAfterAndDateTimeBeforeOrderByDateTimeAsc(questionCandidate.getDateTime(), answerTimeLimit);
            QALabelingModel qaLabelingModel = QALabelingModel.create(questionCandidate, answerCandidates);
            rabbitTemplate.convertAndSend(markDiscussionsQueueName, List.of(qaLabelingModel));
            questionCandidate.setSentToProcess(true);
            messageRepository.save(questionCandidate);
        }
    }
}
