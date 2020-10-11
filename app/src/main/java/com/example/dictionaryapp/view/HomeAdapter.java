package com.example.dictionaryapp.view;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dictionaryapp.R;
import com.example.dictionaryapp.model.Word;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    Cursor cursor;
    private int kindWord;

    public HomeAdapter(Cursor cursor,int kindWord) {
        this.cursor = cursor;
        this.kindWord = kindWord;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String word = cursor.getString(cursor.getColumnIndex("WORD"));
        String meanHtml = cursor.getString(cursor.getColumnIndex("MEAN"));
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        holder.word = new Word(id,word,meanHtml);
        holder.tvWord.setText(word);
        String mean = meanHtml.substring(meanHtml.indexOf("<li>") + 4, meanHtml.indexOf("</li>"));
        holder.tvMean.setText(Html.fromHtml(mean));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvWord;
        private TextView tvMean;
        private Word word;
        public MyViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            this.tvWord = this.view.findViewById(R.id.tv_word);
            this.tvMean = this.view.findViewById(R.id.tv_mean);
            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("word",word);
                    bundle.putInt("kindWord",kindWord);
                    Navigation.findNavController(view).navigate(R.id.detail_word,bundle);
                }
            });
        }
    }
}
