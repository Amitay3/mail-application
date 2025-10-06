package com.example.abamailapp.entities;

public class MailRequest {
    private String sender;
    private String recipient;
    private String subject;
    private String content;
    private boolean isDraft;

    public MailRequest(String sender, String recipient, String subject, String content, boolean isDraft) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.isDraft = isDraft;
    }

    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public boolean isDraft() { return isDraft; }
}
