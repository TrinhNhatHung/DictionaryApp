package com.example.dictionaryapp.ui.vie_eng;

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
import com.example.dictionaryapp.databases.DatabaseVieEngAccess;
import com.example.dictionaryapp.model.Word;
import com.example.dictionaryapp.view.HomeAdapter;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class VieEngFragment extends Fragment {

    private VieEngViewModel vieEngViewModel;
    private View view;
    private RecyclerView rvWord;
    private AutoCompleteTextView edSearch;
    private ImageButton btnVoiceSearch;
    private final int REQ_CODE_SPEECH_INPUT = 101;
    private final int KIND_WORD = 2;
    private HomeAdapter vieEngAdapter;
    private DatabaseVieEngAccess databaseVieEngAccess;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vie_eng, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        rvWord = view.findViewById(R.id.rv_words1);
        edSearch =(AutoCompleteTextView) view.findViewById(R.id.ed_search1);
        btnVoiceSearch = view.findViewById(R.id.btn_voice_search1);
        rvWord.setLayoutManager(new LinearLayoutManager(getContext()));
        vieEngViewModel = new ViewModelProvider(this).get(VieEngViewModel.class);
        vieEngViewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                vieEngAdapter = new HomeAdapter(cursor,KIND_WORD);
                rvWord.setAdapter(vieEngAdapter);
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
                databaseVieEngAccess = DatabaseVieEngAccess.getInstance(getContext());
                databaseVieEngAccess.open();
                Cursor cursor = databaseVieEngAccess.getWordSearch(charSequence.toString());
                VieEngChangeCursorAdapter adapter = new VieEngChangeCursorAdapter(getContext(),cursor,0);
                edSearch.setAdapter(adapter);
                databaseVieEngAccess.close();
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
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
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

    private class VieEngChangeCursorAdapter extends CursorAdapter {
        public VieEngChangeCursorAdapter(Context context, Cursor c, int flags) {
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
            databaseVieEngAccess = DatabaseVieEngAccess.getInstance(getContext());
            databaseVieEngAccess.open();
            Cursor cursor = databaseVieEngAccess.getWords();
            publishProgress(cursor);
            return null;
        }

        @Override
        protected void onProgressUpdate(Cursor... values) {
            super.onProgressUpdate(values);
            vieEngViewModel.loadLiveData(values[0]);
        }
    }
}