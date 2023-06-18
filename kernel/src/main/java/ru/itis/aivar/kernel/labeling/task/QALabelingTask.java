package ru.itis.aivar.kernel.labeling.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itis.aivar.kernel.labeling.service.QALabelingService;

@Component
public class QALabelingTask {

    @Autowired
    QALabelingService qaLabelingService;

    @Scheduled(cron = "0 0/3 * * * ?")
    public void startLabeling() {
        qaLabelingService.startLabeling();
    }
}
