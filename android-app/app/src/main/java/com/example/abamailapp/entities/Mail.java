package com.example.abamailapp.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "mails")
public class Mail {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "backend_id", index = true)
    @SerializedName("_id")
    private String backendId;

    @ColumnInfo(name = "sender_email", index = true)
    @SerializedName("sender")
    private String senderEmail;

    @ColumnInfo(name = "recipient_email", index = true)
    @SerializedName("recipient")
    private String recipientEmail;

    @SerializedName("subject")
    private String subject;

    @SerializedName("content")
    private String content;

    @ColumnInfo(name = "time")
    @SerializedName("timestamp")
    private String time;

    @SerializedName("isDraft")
    private boolean isDraft;

    private boolean isSpam;

    public Mail(String senderEmail, String recipientEmail, String subject, String content,
                String time, boolean isDraft, boolean isSpam, String backendId) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.content = content;
        this.time = time;
        this.isDraft = isDraft;
        this.isSpam = isSpam;
        this.backendId = backendId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBackendId() { return backendId; }
    public void setBackendId(String backendId) { this.backendId = backendId; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isDraft() { return isDraft; }
    public void setDraft(boolean draft) { isDraft = draft; }

    public boolean isSpam() { return isSpam; }
    public void setSpam(boolean spam) { isSpam = spam; }
}
