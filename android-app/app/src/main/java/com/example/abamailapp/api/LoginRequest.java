package com.example.abamailapp.api;

public class LoginRequest {
    private String mailAddress;
    private String password;

    public LoginRequest(String mailAddress, String password) {
        this.mailAddress = mailAddress;
        this.password = password;
    }

    public String getMailAddress() { return mailAddress; }
    public String getPassword() { return password; }
}
