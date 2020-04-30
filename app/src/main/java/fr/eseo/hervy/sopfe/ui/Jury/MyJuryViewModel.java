package fr.eseo.hervy.sopfe.ui.Jury;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created on 25/09/2019 - 14:05.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : MyJuryViewModel
 */
public class MyJuryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyJuryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my jury fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}