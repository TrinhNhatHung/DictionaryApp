package com.example.dictionaryapp.ui.favorites;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dictionaryapp.model.Word;

import java.util.List;

public class FavoriteVietnameseViewModel extends ViewModel {
    private MutableLiveData<List<Word>> livedata;

    public MutableLiveData<List<Word>>  getLivedata (){
        if (livedata  == null){
            livedata = new MutableLiveData<List<Word>>();
        }
        return livedata;
    }
    public void loadLiveData (List<Word> mWord){
        livedata.setValue(mWord);
    }
}
