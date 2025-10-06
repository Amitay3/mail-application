package com.example.abamailapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailLabelCrossRef;
import com.example.abamailapp.entities.User;

@Database(entities = {Mail.class, User.class, Label.class, MailLabelCrossRef.class}, version = 9)
public abstract class AppDB extends RoomDatabase {
    public abstract MailDao mailDao();
    public abstract UserDao userDao();
    public abstract LabelDao labelDao();
    public abstract MailLabelDao mailLabelDao();

}
