package com.example.foodapp.model;

public class Chat {
    private long id;
    private String senderId;
    private String receiverId;
    private String message;
    private String timeStamp;

    public Chat() {
    }

    public Chat(long id, String senderId, String receiverId, String content, String timeStamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = content;
        this.timeStamp = timeStamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
