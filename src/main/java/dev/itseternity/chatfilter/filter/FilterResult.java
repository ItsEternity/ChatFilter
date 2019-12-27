package dev.itseternity.chatfilter.filter;

import lombok.Data;
import lombok.Getter;

@Data
public class FilterResult {

    private final boolean shouldCancel;
    private final boolean messageChanged;
    private final String message;

}
