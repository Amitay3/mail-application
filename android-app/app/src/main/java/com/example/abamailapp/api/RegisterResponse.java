package com.example.abamailapp.entities;

public class RegisterResponse {
    private String userName;
    private String password;
    private String mailAddress;

    public User toUser() {
        return new User(userName, password, mailAddress);
    }
}
