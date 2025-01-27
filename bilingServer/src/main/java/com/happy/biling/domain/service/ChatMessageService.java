package com.happy.biling.domain.service;

import com.happy.biling.domain.entity.ChatMessage;
import com.happy.biling.domain.entity.ChatRoom;
import com.happy.biling.domain.entity.Post;
import com.happy.biling.domain.entity.User;
import com.happy.biling.domain.repository.ChatMessageRepository;
import com.happy.biling.domain.repository.ChatRoomRepository;
import com.happy.biling.domain.repository.PostRepository;
import com.happy.biling.domain.repository.UserRepository;
import com.happy.biling.dto.chat.ChatRoomResponse;
import com.happy.biling.dto.chat.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void sendMessage(SendMessageRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (request.getOwnerId() == null || request.getRenterId() == null) {
            throw new IllegalArgumentException("Owner ID and Renter ID must not be null");
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        User renter = userRepository.findById(request.getRenterId())
                .orElseThrow(() -> new RuntimeException("Renter not found"));

        ChatRoom chatRoom = chatRoomRepository.findByPostAndOwnerAndRenter(post, owner, renter);

        if (chatRoom == null) {
            chatRoom = new ChatRoom();
            chatRoom.setPost(post);
            chatRoom.setOwner(owner);
            chatRoom.setRenter(renter);
            chatRoom = chatRoomRepository.save(chatRoom);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(owner);
        chatMessage.setContent(request.getContent());
        chatMessage.setIsRead(false);

        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public List<ChatRoomResponse> getChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChatRoom> chatRooms = chatRoomRepository.findByOwnerOrRenter(user, user);

        return chatRooms.stream().map(chatRoom -> {
            User otherUser = chatRoom.getOwner().equals(user) ? chatRoom.getRenter() : chatRoom.getOwner();

            ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderByCreateAtDesc(chatRoom)
                    .orElse(null);

            return new ChatRoomResponse(
                    otherUser.getProfileImage(),
                    chatRoom.getPost().getTitle(),
                    lastMessage != null ? lastMessage.getContent() : "No messages",
                    chatRoom.getUnreadCount(),
                    lastMessage != null ? lastMessage.getCreateAt() : LocalDateTime.now()
            );
        }).collect(Collectors.toList());
    }
}
