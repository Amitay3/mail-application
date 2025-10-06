package com.example.abamailapp;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailLabelCrossRef;

import java.util.List;

public class MailWithLabels {
    @Embedded
    public Mail mail;

    @Relation(
            parentColumn = "backend_id",
            entityColumn = "backendId",
            associateBy = @Junction(
                    value = MailLabelCrossRef.class,
                    parentColumn = "mailBackendId",
                    entityColumn = "labelBackendId"
            )
    )
    public List<Label> labels;
}

