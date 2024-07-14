package com.jitendract.jitdemo.AppInboxModel;

import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.clevertap.android.sdk.inbox.CTInboxMessageContent;

import java.util.List;

public class InboxDataHelper {

    private final CTInboxMessage message;

    public InboxDataHelper(CTInboxMessage message) {
        this.message = message;
    }

    public String getMessageType() {
        return String.valueOf(message.getType());
    }

    public boolean isMessageRead() {
        return message.isRead();
    }

    public String getMessageId() {
        return message.getMessageId();
    }

    public String getBackgroundColor() {
        return message.getBgColor();
    }

    public String getMessageColor() {
        List<?> contents = message.getInboxMessageContents();
        if (contents != null && contents.size() > 0 && contents.get(0) instanceof CTInboxMessageContent) {
            return ((CTInboxMessageContent) contents.get(0)).getMessageColor();
        }
        return null;
    }

    public String getTitleColor() {
        List<?> contents = message.getInboxMessageContents();
        if (contents != null && contents.size() > 0 && contents.get(0) instanceof CTInboxMessageContent) {
            return ((CTInboxMessageContent) contents.get(0)).getTitleColor();
        }
        return null;
    }

    public String getMessageText() {
        List<?> contents = message.getInboxMessageContents();
        if (contents != null && contents.size() > 0 && contents.get(0) instanceof CTInboxMessageContent) {
            return ((CTInboxMessageContent) contents.get(0)).getMessage();
        }
        return null;
    }

    public String getTitleText() {
        List<?> contents = message.getInboxMessageContents();
        if (contents != null && contents.size() > 0 && contents.get(0) instanceof CTInboxMessageContent) {
            return ((CTInboxMessageContent) contents.get(0)).getTitle();
        }
        return null;
    }

    public String getImageUrl() {
        List<?> contents = message.getInboxMessageContents();
        if (contents != null && contents.size() > 0 && contents.get(0) instanceof CTInboxMessageContent) {
            return ((CTInboxMessageContent) contents.get(0)).getMedia();
        }
        return null;
    }

    public String getIconUrl() {
        List<?> contents = message.getInboxMessageContents();
        if (contents != null && contents.size() > 0 && contents.get(0) instanceof CTInboxMessageContent) {
            return ((CTInboxMessageContent) contents.get(0)).getIcon();
        }
        return null;
    }
}
