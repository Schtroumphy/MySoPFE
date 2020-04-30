package fr.eseo.hervy.sopfe.ui.allProjects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllProjectsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllProjectsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my projects fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}