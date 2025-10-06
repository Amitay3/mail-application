package com.example.abamailapp;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.User;

import java.util.List;

public class UserWithLabels {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "id",
            entityColumn = "userId"
    )
    public List<Label> labels;
}

