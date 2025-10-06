package com.example.abamailapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.repositories.LabelRepository;

import java.util.List;

public class LabelViewModel extends AndroidViewModel {

    private final LabelRepository repository;

    public LabelViewModel(@NonNull Application application) {
        super(application);
        repository = new LabelRepository(application);
    }

    public LiveData<List<Label>> getLabels() {
        return repository.getLabels();
    }

    public void addLabel(String labelName) {
        repository.addLabel(labelName);
    }

    public void updateLabel(Label label) {
        repository.updateLabel(label);
    }

    public void deleteLabel(Label label) {
        repository.deleteLabel(label);
    }

    public void refresh() {
        repository.fetchLabelsFromBackend();
    }
}
