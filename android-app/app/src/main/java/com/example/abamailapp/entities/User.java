package com.example.abamailapp.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "mail_address")
    private String mailAddress;

    @ColumnInfo(name = "image")
    private String image;

    public User(String userName, String password, String mailAddress, String image) {
        this.userName = userName;
        this.password = password;
        this.mailAddress = mailAddress;
        this.image = image;
    }

    @Ignore
    public User(String userName, String password, String mailAddress) {
        this.userName = userName;
        this.password = password;
        this.mailAddress = mailAddress;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

}
