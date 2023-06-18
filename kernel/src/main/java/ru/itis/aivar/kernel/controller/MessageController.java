package ru.itis.aivar.kernel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itis.aivar.kernel.dto.MessageToReceive;
import ru.itis.aivar.kernel.labeling.model.QALabelingModel;
import ru.itis.aivar.kernel.service.MessageService;

import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/receive")
    public void receive(@RequestBody List<MessageToReceive> messagesToReceive) {
        messageService.saveAfterLastSavedMessage(messagesToReceive);
    }

    @GetMapping("/processed")
    @ResponseBody
    public List<QALabelingModel> processed() {
        return messageService.getAllProcessedDiscussions();
    }

    @GetMapping("/upload")
    public String load(Model model) {
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        List<MessageToReceive> messagesToReceive = messageService.processChatLogFile(file);
        messageService.saveAfterLastSavedMessage(messagesToReceive);

        return "redirect:/upload";
    }
}
