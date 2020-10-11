package com.example.dictionaryapp.ui.vie_eng;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class VieEngViewModel extends ViewModel {

    private MutableLiveData<Cursor> mWords;
    public LiveData<Cursor> getLiveData (){
        if (mWords == null){
            mWords = new MutableLiveData<Cursor>();
        }
        return mWords;
    }

    public void loadLiveData (Cursor cursor){
        mWords.setValue(cursor);
    }
}