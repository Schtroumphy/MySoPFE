package fr.eseo.hervy.sopfe.ui.myProjects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyProjectsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyProjectsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all projects fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}