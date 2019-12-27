package dev.itseternity.chatfilter.chat;

import lombok.Data;

@Data
class ChatMessage {

    private final String message;
    private final long timestamp;

}
