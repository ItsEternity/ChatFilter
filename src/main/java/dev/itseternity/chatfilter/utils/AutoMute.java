package dev.itseternity.chatfilter.utils;

import lombok.Data;

import java.util.UUID;

@Data
class AutoMute {

    private final UUID uuid;
    private final long timestamp;

}
