package com.example.abamailapp.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "labels")
public class Label {
    @PrimaryKey(autoGenerate = true)
    private int id;  // local Room ID (just for your DB)

    @ColumnInfo(name = "backendId")
    @SerializedName("_id")
    private String backendId;  // MongoDB _id from backend

    @SerializedName("name")
    private String name;

    private int userId;

    public Label(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBackendId() { return backendId; }
    public void setBackendId(String backendId) { this.backendId = backendId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}


