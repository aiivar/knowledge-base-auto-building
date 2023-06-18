package ru.itis.aivar.kernel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.itis.aivar.kernel.entity.Message;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionCandidateService {

    @Value("classpath:questionCandidateWords.txt")
    private Resource questionWords;

    public boolean isQuestionCandidate(Message message) {
        try {
            String questionWordsStr = new BufferedReader(new FileReader(questionWords.getFile())).readLine();
            Set<String> questionWords = Arrays.stream(questionWordsStr.split(",")).collect(Collectors.toSet());
            return message.getText() != null && message.getText().length() > 80 && questionWords.stream().anyMatch(word -> message.getText().contains(word));
        } catch (IOException e) {
            return false;
        }
    }

}
