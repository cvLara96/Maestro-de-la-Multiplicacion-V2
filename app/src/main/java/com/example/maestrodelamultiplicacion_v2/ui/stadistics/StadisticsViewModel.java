package com.example.maestrodelamultiplicacion_v2.ui.stadistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StadisticsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StadisticsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}