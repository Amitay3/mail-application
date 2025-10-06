package com.example.abamailapp.entities;

import com.google.gson.annotations.SerializedName;

public class CreateLabelRequest {
    @SerializedName("labelName")
    private String labelName;

    public CreateLabelRequest(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelName() { return labelName; }
}

