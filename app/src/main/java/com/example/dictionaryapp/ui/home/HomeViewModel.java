package com.example.dictionaryapp.ui.home;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dictionaryapp.model.Word;

import java.util.List;

public class HomeViewModel extends ViewModel {

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