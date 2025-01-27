package com.happy.biling.controller;

import com.happy.biling.dto.chat.SendMessageRequest;
import com.happy.biling.dto.chat.ChatRoomResponse;
import com.happy.biling.domain.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequest request) {
        chatMessageService.sendMessage(request);
        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@RequestParam Long userId) {
        List<ChatRoomResponse> chatRooms = chatMessageService.getChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }
}
