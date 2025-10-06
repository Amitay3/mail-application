package com.example.abamailapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.abamailapp.entities.Label;

import java.util.List;

@Dao
public interface LabelDao {

    @Query("SELECT * FROM labels")
    List<Label> getAllLabels();

    @Query("SELECT * FROM labels WHERE id = :id")
    Label get(int id);

    @Insert
    void insert(Label... labels);
    @Insert
    void insert(List<Label> labels);

    @Update
    void update(Label... labels);

    @Delete
    void delete(Label... labels);

    @Query("SELECT * FROM labels WHERE userId = :userId ORDER BY name ASC")
    LiveData<List<Label>> getLabelsForUser(int userId);

    @Query("SELECT * FROM labels ORDER BY name ASC")
    LiveData<List<Label>> getAllLabelsLive();

    @Query("DELETE FROM labels WHERE userId = :userId")
    void deleteLabelsForUser(int userId);

    @Query("DELETE FROM labels")
    void deleteAll();

    @Query("SELECT * FROM labels WHERE backendId = :id LIMIT 1")
    Label getLabelById(String id);
}
