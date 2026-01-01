package com.irem.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class OllamaController {
    private ChatClient chatClient;

    //@Autowired
    //@Qualifier("ollamaEmbedingModel")
    //private EmbeddingModel embeddingModel;


    public OllamaController(OllamaChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }


    // ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

//    public OllamaController(ChatClient.Builder builder) {
//        this.chatClient = builder
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
//                        .build())
//                .build();
//
//    }

    @GetMapping("/api/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message) {
        ChatResponse chatResponse = chatClient.prompt(message)
                .call()
                .chatResponse();

        System.out.println(chatResponse.getMetadata().getModel());


        String response = chatResponse
                .getResult()
                .getOutput()
                .getText();
        return ResponseEntity.ok(response);
    }

    //nasıl kullanıcam anlamadım. kelimeleri vektöre çeviriyor. Cosine similarity belki
   // @PostMapping("/api/embedding")
   // public float[] embedding(@RequestBody String text) {
   //     return embeddingModel.embed(text);    
   // }
    
}