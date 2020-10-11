package com.example.dictionaryapp.ui.favorites;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.databases.DatabaseEngVieAccess;
import com.example.dictionaryapp.model.Word;
import com.example.dictionaryapp.view.FavoriteWordAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEnglishFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int KIND_WORD = 3;
    private RecyclerView rvFavoriteWord;
    private ArrayList<Word> mWords;
    private FavoriteEnglishViewModel favoritesViewModel;
    private FavoriteWordAdapter favoriteWordAdapter;

    public FavoriteEnglishFragment() {

    }

    public static FavoriteEnglishFragment newInstance(String param1, String param2) {
        FavoriteEnglishFragment fragment = new FavoriteEnglishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_english, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFavoriteWord = view.findViewById(R.id.rv_word_favorite);
        rvFavoriteWord.setLayoutManager(new LinearLayoutManager(getContext()));

        favoritesViewModel = new ViewModelProvider(this).get(FavoriteEnglishViewModel.class);
        favoritesViewModel.getLivedata().observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                favoriteWordAdapter = new FavoriteWordAdapter((ArrayList<Word>) words,KIND_WORD);
                rvFavoriteWord.setAdapter(favoriteWordAdapter);
            }
        });
        new AsyncLoadData().execute();
    }

        private class AsyncLoadData extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseEngVieAccess databaseAccess = DatabaseEngVieAccess.getInstance(getContext());
            databaseAccess.open();
            mWords = (ArrayList<Word>) databaseAccess.getFavoriteEnglishWords();
            databaseAccess.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            favoritesViewModel.loadLiveData(mWords);
        }
    }
}