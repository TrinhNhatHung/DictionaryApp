package com.example.dictionaryapp.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.databases.DatabaseEngVieAccess;
import com.example.dictionaryapp.model.Word;
import com.example.dictionaryapp.view.HomeAdapter;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView rvWord;
    private HomeAdapter homeAdapter;
    private AutoCompleteTextView edSearch;
    private ImageButton btnVoiceSearch;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int KIND_WORD = 1;
    private View view;
    private DatabaseEngVieAccess databaseEngVieAccess;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        rvWord = view.findViewById(R.id.rv_words);
        edSearch =(AutoCompleteTextView) view.findViewById(R.id.ed_search);
        btnVoiceSearch = view.findViewById(R.id.btn_voice_search);
        rvWord.setLayoutManager(new LinearLayoutManager(getContext()));
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                homeAdapter = new HomeAdapter(cursor,KIND_WORD);
                rvWord.setAdapter(homeAdapter);
            }
        });

        new AsyncLoadData().execute();

        btnVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                databaseEngVieAccess = DatabaseEngVieAccess.getInstance(getContext());
                databaseEngVieAccess.open();
                Cursor cursor = databaseEngVieAccess.getWordSearch(charSequence.toString());
                HomeChangeCursorAdapter adapter = new HomeChangeCursorAdapter(getContext(),cursor,0);
                edSearch.setAdapter(adapter);
                databaseEngVieAccess.close();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Pronunciate word");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(), "Sorry! Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edSearch.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void navigateToDetailWordFragment (Word word){
        Bundle bundle = new Bundle();
        bundle.putSerializable("word", word);
        bundle.putInt("kindWord",KIND_WORD);
        Navigation.findNavController(view).navigate(R.id.detail_word,bundle);
    }

    private class HomeChangeCursorAdapter extends CursorAdapter {
        public HomeChangeCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textViewTitle = (TextView) view.findViewById(android.R.id.text1);
            String title = cursor.getString(cursor.getColumnIndex("WORD"));
            String mean = cursor.getString(cursor.getColumnIndex("MEAN"));
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            final Word word = new Word(id,title,mean);
            textViewTitle.setText(title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToDetailWordFragment(word);
                }
            });
        }
    }

    private class AsyncLoadData extends AsyncTask<Void,Cursor,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseEngVieAccess = DatabaseEngVieAccess.getInstance(getContext());
            databaseEngVieAccess.open();
            Cursor cursor = databaseEngVieAccess.getWords();
            publishProgress(cursor);
            return null;
        }

        @Override
        protected void onProgressUpdate(Cursor... values) {
            super.onProgressUpdate(values);
            homeViewModel.loadLiveData(values[0]);
        }
    }
}