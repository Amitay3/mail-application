package com.example.abamailapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailLabelCrossRef;

import java.util.List;

@Dao
public interface MailLabelDao {

    // Add label to mail
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addLabelToMail(MailLabelCrossRef crossRef);

    // Remove label from mail
    @Query("DELETE FROM MailLabelCrossRef WHERE mailBackendId = :mailId AND labelBackendId = :labelId AND userId = :userId")
    void removeLabelFromMail(String mailId, String labelId, int userId);

    // Get all labels for a specific mail
    @Transaction
    @Query("SELECT * FROM mails WHERE backend_id = :mailId")
    LiveData<MailWithLabels> getAllLabelsForMail(String mailId);

    // Check if a mail has a specific label
    @Transaction
    @Query("SELECT * FROM mails WHERE backend_id = :mailId AND EXISTS (" +
            "SELECT 1 FROM MailLabelCrossRef WHERE mailBackendId = :mailId AND labelBackendId = :labelId)")
    LiveData<MailWithLabels> getMailWithSpecificLabel(String mailId, String labelId);

    // Get mails for a label
    @Transaction
    @Query("SELECT * FROM labels WHERE id = :labelId AND userId = :userId")
    LiveData<LabelWithMails> getMailsForLabel(int labelId, int userId);

    @Transaction
    @Query("SELECT * FROM mails WHERE backend_id = :mailId")
    MailWithLabels getAllLabelsForMailSync(String mailId);

    @Transaction
    @Query("SELECT m.* FROM mails m " +
            "INNER JOIN MailLabelCrossRef mlc ON m.backend_id = mlc.mailBackendId " +
            "WHERE mlc.labelBackendId = :labelBackendId AND mlc.userId = :userId")
    LiveData<List<Mail>> getMailsForLabelSync(String labelBackendId, int userId);

}

