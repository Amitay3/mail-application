package com.example.abamailapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.abamailapp.entities.Mail;

import java.util.List;

@Dao
public interface MailDao {

    @Query("DELETE FROM mails WHERE backend_id IS NULL")
    void deleteMailsWithNullBackendId();
    @Query("DELETE FROM mails WHERE backend_id = :backendId")
    void deleteByBackendId(String backendId);
    @Query("SELECT * FROM mails")
    List<Mail> index();

    @Query("SELECT * FROM mails WHERE id = :id")
    Mail get(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mail mail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Mail> mails);

    @Update
    void update(Mail... mails);

    @Delete
    void delete(Mail... mails);

    // --- Updated queries using email strings ---

    @Query("SELECT * FROM mails WHERE recipient_email = :email AND isDraft = 0 AND isSpam = 0")
    LiveData<List<Mail>> getInbox(String email);

    @Query("SELECT * FROM mails WHERE sender_email = :email AND isDraft = 0 AND isSpam = 0")
    LiveData<List<Mail>> getSent(String email);

    @Query("SELECT * FROM mails WHERE sender_email = :email AND isDraft = 1")
    LiveData<List<Mail>> getDrafts(String email);

    @Query("SELECT * FROM mails WHERE recipient_email = :email AND isSpam = 1")
    LiveData<List<Mail>> getSpam(String email);

    @Query("DELETE FROM mails WHERE recipient_email = :userEmail")
    void deleteInboxForUser(String userEmail);


    @Query("DELETE FROM mails WHERE sender_email = :userEmail")
    void deleteSentForUser(String userEmail);

    @Query("DELETE FROM mails WHERE sender_email = :userEmail AND isDraft = 1")
    void deleteDraftsForUser(String userEmail);

    @Query("DELETE FROM mails WHERE (recipient_email = :userEmail OR sender_email  = :userEmail) AND isSpam = 1")
    void deleteSpamForUser(String userEmail);

    @Query("SELECT * FROM mails WHERE backend_id = :backendId LIMIT 1")
    Mail getByBackendId(String backendId);

    @Query("SELECT * FROM mails " +
            "WHERE id IN (" +
            "SELECT id FROM mails " +
            "WHERE subject LIKE '%' || :query || '%' " +
            "OR content LIKE '%' || :query || '%' " +
            "OR sender_email LIKE '%' || :query || '%' " +
            "OR recipient_email LIKE '%' || :query || '%'" +
            ")")
    LiveData<List<Mail>> searchMails(String query);

}
