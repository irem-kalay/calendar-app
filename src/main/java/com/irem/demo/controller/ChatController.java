package com.irem.demo.controller;

import com.irem.demo.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        // frontend'den gelen veriler
        String message = body.get("message");
        Long regionId = Long.valueOf(body.getOrDefault("regionId", "1"));
        Long personTypeId = Long.valueOf(body.getOrDefault("personTypeId", "1"));

        // ChatService metodunu 3 parametre ile çağır
        String reply = chatService.getReply(message, regionId, personTypeId);

        return ResponseEntity.ok(Collections.singletonMap("reply", reply));
    }
}
