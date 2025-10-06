package com.example.abamailapp.api;

public class MailLabelRequest {
    private String mailId;
    private String labelId;

    public MailLabelRequest(String mailId, String labelId) {
        this.mailId = mailId;
        this.labelId = labelId;
    }

    public String getMailId() { return mailId; }
    public String getLabelId() { return labelId; }
}
