package com.example.dictionaryapp.ui.yourword;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.databases.DatabaseEngVieAccess;
import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YourWordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourWordFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView tvWord;
    private TextView tvScore;
    private TextView tvLabelScore;
    private ImageButton btnMic;
    private DatabaseEngVieAccess databaseAccess;
    private Word word;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    // TODO: Rename and change types of parameters
    public YourWordFragment() {

    }

    public static YourWordFragment newInstance(String param1, String param2) {
        YourWordFragment fragment = new YourWordFragment();
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
        return inflater.inflate(R.layout.fragment_nav_your_word, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvWord = view.findViewById(R.id.tv_your_word);
        tvLabelScore = view.findViewById(R.id.tv_label);
        tvScore = view.findViewById(R.id.tv_score);
        btnMic = view.findViewById(R.id.btn_mic);
        new AsyncQuerryData().execute();

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        view.setOnTouchListener(new OnSwipeListener(getContext()){
            @Override
            public void onSwipeRight() {
                new AsyncQuerryData().execute();
                tvScore.setText("");
                tvLabelScore.setText("");
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
                    tvScore.setText(""  + samePercentage(word.getWord(),result.get(0)) + " đ");
                    tvLabelScore.setText("Your score");
                }
                break;
            }
        }
    }

    public float samePercentage (String originalString, String inputString){
        int sameCount =0;
        int length = (originalString.length() > inputString.length()) ? inputString.length() : originalString.length();
        for (int i=0; i< length;i++){
            if (originalString.charAt(i) == inputString.charAt(i)){
                sameCount ++;
            }
        }
        float result = ((float) sameCount / originalString.length()) *10;
        return Math.round(result * 10)/10;
    }

    public class AsyncQuerryData extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tvWord.setText(word.getWord());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            databaseAccess = DatabaseEngVieAccess.getInstance(getContext());
            databaseAccess.open();
            int count = databaseAccess.getRowCount();
            Random random = new Random();
            word = databaseAccess.getOneWord(random.nextInt(count));
            databaseAccess.close();
            return null;
        }
    }

    public class OnSwipeListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;

        public OnSwipeListener(Context context) {
             this.gestureDetector = new GestureDetector(context, new GestureListener());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener{
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        public void onSwipeRight() {

        }

        public void onSwipeLeft(){

        }

        public  void onSwipeBottom(){

        }

        public  void onSwipeTop(){

        }
    }
}
