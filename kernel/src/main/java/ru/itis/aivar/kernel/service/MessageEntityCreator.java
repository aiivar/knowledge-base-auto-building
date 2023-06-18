package ru.itis.aivar.kernel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.aivar.kernel.dto.MessageToReceive;
import ru.itis.aivar.kernel.entity.Message;

@Service
public class MessageEntityCreator implements EntityCreator<Message> {

    @Autowired
    private QuestionCandidateService questionCandidateService;

    @Override
    public Message newInstance() {
        return Message.builder().build();
    }

    public Message newInstance(MessageToReceive message) {
        Message newInstance = Message.newInstance(message);
        newInstance.setQuestionCandidate(questionCandidateService.isQuestionCandidate(newInstance));
        return newInstance;
    }

}
