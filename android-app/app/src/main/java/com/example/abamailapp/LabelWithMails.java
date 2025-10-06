package com.example.abamailapp;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailLabelCrossRef;

import java.util.List;

public class LabelWithMails {
    @Embedded
    public Label label;

    @Relation(
            parentColumn = "backendId",
            entityColumn = "backend_id",
            associateBy = @Junction(
                    value = MailLabelCrossRef.class,
                    parentColumn = "labelBackendId",
                    entityColumn = "mailBackendId"
            )
    )
    public List<Mail> mails;
}