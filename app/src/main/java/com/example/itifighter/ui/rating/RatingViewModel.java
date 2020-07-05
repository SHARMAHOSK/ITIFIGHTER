package com.example.itifighter.ui.rating;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RatingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RatingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Change Password fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}