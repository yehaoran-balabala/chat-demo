package com.example.chat_demo.controller;

import com.example.chat_demo.entity.Message;
import com.example.chat_demo.service.IMessageService;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
public class MessageController {
    @Autowired
    private final OllamaChatModel ollamaChatModel;

    public MessageController(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    @Autowired
    private IMessageService messageService;

    @GetMapping("/messages")
    public List<Message> getMessage(){
        return messageService.list();
    }

    @GetMapping("/stream")
    public Flux<ServerSentEvent<String>> chat(@RequestParam("prompt") String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "prompt不能为空");
        }
        //用户消息
        Message umsg = new Message();
        umsg.setCreatedAt(LocalDateTime.now());
        umsg.setContent(prompt);
        umsg.setSender("user");
        messageService.save(umsg);

        StringBuilder aiResponseBuilder = new StringBuilder();

        //token
        return ollamaChatModel.stream(prompt).map(token -> {
            aiResponseBuilder.append(token);
            return ServerSentEvent.builder(token).event("message").build();
        }).doOnComplete(()->{
            System.out.println(aiResponseBuilder);
            Message aiMsg = new Message();
            aiMsg.setCreatedAt(LocalDateTime.now());
            aiMsg.setContent(aiResponseBuilder.toString());
            aiMsg.setSender("bot");
            messageService.save(aiMsg);
        });
    }
}
