package ru.itis.aivar.kernel.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.aivar.kernel.dto.MessageToReceive;
import ru.itis.aivar.kernel.entity.Chat;
import ru.itis.aivar.kernel.entity.Message;
import ru.itis.aivar.kernel.entity.QAEntity;
import ru.itis.aivar.kernel.labeling.model.QALabelingModel;
import ru.itis.aivar.kernel.repository.ChatRepository;
import ru.itis.aivar.kernel.repository.MessageRepository;
import ru.itis.aivar.kernel.repository.QAEntityRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageEntityCreator messageEntityCreator;

    @Autowired
    private QAEntityRepository qaEntityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveAfterLastSavedMessage(List<MessageToReceive> messagesToReceive) {
        Map<String, Chat> allSavedChats = chatRepository.findAll().stream().collect(Collectors.toMap(
                Chat::getExtId,
                chat -> chat
        ));
        Map<String, List<MessageToReceive>> chatExtIdToMessages = messagesToReceive.stream().collect(Collectors.groupingBy(MessageToReceive::getChatId));
        for (Map.Entry<String, List<MessageToReceive>> entry : chatExtIdToMessages.entrySet()) {
            Set<String> allSavedChatsExtIds = allSavedChats.keySet();
            String chatExtId = entry.getKey();
            if (allSavedChatsExtIds.contains(chatExtId)) {
                Chat chat = allSavedChats.get(chatExtId);
                Optional<Message> currentMessage = chat.getMessages().stream()
                        .sorted(Comparator.comparing(Message::getDateTime))
                        .reduce((first, second) -> second);

                currentMessage.ifPresentOrElse(
                        message -> {
                            LocalDateTime lastDateTime = message.getDateTime();
                            entry.getValue().stream()
                                    .map(messageEntityCreator::newInstance)
                                    .filter(msg -> msg.getDateTime().isAfter(lastDateTime))
                                    .forEach(chat::addMessage);
                        },
                        () -> entry.getValue().stream()
                                .map(messageEntityCreator::newInstance)
                                .forEach(chat::addMessage)
                );
                chatRepository.save(chat);
            } else {
                Chat chat = Chat.newInstance(chatExtId);
                entry.getValue().stream()
                        .map(messageEntityCreator::newInstance)
                        .forEach(chat::addMessage);
                chatRepository.save(chat);
            }
        }
    }

    public List<QALabelingModel> getAllProcessedDiscussions() {
        return qaEntityRepository.findAll().stream().map(QAEntity::from).collect(Collectors.toList());
    }

    public List<MessageToReceive> processChatLogFile(MultipartFile chatLogFile) {
        if (!Objects.equals(chatLogFile.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(chatLogFile.getBytes(), new TypeReference<List<MessageToReceive>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
