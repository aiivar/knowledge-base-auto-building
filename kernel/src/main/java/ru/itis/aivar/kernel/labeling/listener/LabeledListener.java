package ru.itis.aivar.kernel.labeling.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itis.aivar.kernel.entity.Message;
import ru.itis.aivar.kernel.entity.QAEntity;
import ru.itis.aivar.kernel.labeling.model.QALabelingModel;
import ru.itis.aivar.kernel.repository.MessageRepository;
import ru.itis.aivar.kernel.repository.QAEntityRepository;

import java.util.List;

@Component
public class LabeledListener {

    @Autowired
    private QAEntityRepository qaEntityRepository;

    @Autowired
    private MessageRepository messageRepository;

    @RabbitListener(queues = "labeled_discussions")
    public void acceptLabeledQA(List<QALabelingModel> qaLabelingModels) {
        for (QALabelingModel qaLabelingModel : qaLabelingModels) {
            QAEntity qaEntity = QAEntity.newInstance(qaLabelingModel);
            Message message = messageRepository.findById(qaLabelingModel.getQuestion().getMessageId()).orElseThrow(IllegalStateException::new);
            message.setProcessed(true);
            messageRepository.save(message);
            qaEntityRepository.save(qaEntity);
        }
    }
}
