package com.example.abamailapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"mailBackendId", "labelBackendId"})
public class MailLabelCrossRef {
    @NonNull
    public String mailBackendId;

    @NonNull
    public String labelBackendId;

    public int userId;
}
