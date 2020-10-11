package com.example.dictionaryapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionaryapp.databases.DatabaseEngVieAccess;
import com.example.dictionaryapp.databases.DatabaseVieEngAccess;
import com.example.dictionaryapp.model.Word;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class DetailWord extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Word word;
    private TextView tvMeanDetail;
    private FloatingActionButton fabPronunciate;
    private  FloatingActionButton fabLike;
    private TextToSpeech textToSpeech;
    private TextView tvWord;
    private int kindWord;
    boolean isFavorite;
    private DatabaseVieEngAccess databaseVieEngAccess;
    private DatabaseEngVieAccess databaseEngVieAccess;

    public DetailWord() {

    }

    public static DetailWord newInstance(String param1, String param2) {
        DetailWord fragment = new DetailWord();
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
            word = (Word) getArguments().getSerializable("word");
            kindWord = getArguments().getInt("kindWord");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_word, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabLike = view.findViewById(R.id.fab_like);
        fabPronunciate = view.findViewById(R.id.fab_pronunciation);
        tvWord = view.findViewById(R.id.tv_word_detail);
        tvWord.setText(word.getWord());
        tvMeanDetail = view.findViewById(R.id.tv_mean_detail);
        tvMeanDetail.setText(Html.fromHtml(word.getMean()));
        tvMeanDetail.setMovementMethod(new ScrollingMovementMethod());

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    if (kindWord ==1 || kindWord == 3){
                        textToSpeech.setLanguage(Locale.US);
                    } else {
                        textToSpeech.setLanguage(Locale.getDefault());
                    }
                }
            }
        });

        textToSpeech.setLanguage(Locale.ENGLISH);

        fabPronunciate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(word.getWord(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        if (kindWord ==3 || kindWord ==4){
           isFavorite = true;
        }

        if (kindWord == 2){
            openDatabaseVieEng();
            isFavorite = databaseVieEngAccess.checkFavorite(word.getId());
            closeDatabaseVieEng();
        }

        if (kindWord == 1){
            openDatabaseEngVie();
            isFavorite = databaseEngVieAccess.checkFavorite(word.getId());
            closeDatabaseEngVie();
        }


        if (isFavorite){
            setLikedButton();
        } else {
            setDisLikedButton();
        }

        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite){
                    setDisLikedButton();
                    isFavorite = false;
                    if (kindWord == 3 || kindWord ==1){
                        openDatabaseEngVie();
                        databaseEngVieAccess.deleteFavoriteWord(word.getId());
                        closeDatabaseEngVie();
                    }
                    if (kindWord == 4 || kindWord ==2){
                        openDatabaseVieEng();
                        databaseVieEngAccess.deleteFavorite(word.getId());
                        closeDatabaseVieEng();
                    }
                    Toast.makeText(getContext(),"Disliked",Toast.LENGTH_SHORT).show();
                } else {
                    setLikedButton();
                    isFavorite = true;
                    if (kindWord == 3 || kindWord ==1){
                        openDatabaseEngVie();
                        databaseEngVieAccess.insertFavoriteWord(word.getId());
                        closeDatabaseEngVie();
                    }
                    if (kindWord == 4 || kindWord ==2){
                        openDatabaseVieEng();
                        databaseVieEngAccess.insertFavoriteWord(word.getId());
                        closeDatabaseVieEng();
                    }
                    Toast.makeText(getContext(),"Liked",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setLikedButton (){
        fabLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        fabLike.setColorFilter(Color.parseColor("#FF075B"));
    }
    private void setDisLikedButton (){
        fabLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF075B")));
        fabLike.setColorFilter(Color.parseColor("#FFFFFF"));
    }
    private void openDatabaseEngVie (){
        databaseEngVieAccess = DatabaseEngVieAccess.getInstance(getContext());
        databaseEngVieAccess.open();
    }

    private  void closeDatabaseEngVie (){
        databaseEngVieAccess.close();
    }

    private void openDatabaseVieEng (){
        databaseVieEngAccess = DatabaseVieEngAccess.getInstance(getContext());
        databaseVieEngAccess.open();
    }

    private  void closeDatabaseVieEng (){
        databaseVieEngAccess.close();
    }
}