package com.example.dictionaryapp.view;

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

import java.util.ArrayList;

public class FavoriteWordAdapter extends RecyclerView.Adapter<FavoriteWordAdapter.FavoriteWordViewHolder> {
    private ArrayList<Word> mFavoriteWord;
    private int kindWord;
    public FavoriteWordAdapter(ArrayList<Word> mFavoriteWord,int kindWord) {
        this.mFavoriteWord = mFavoriteWord;
        this.kindWord = kindWord;
    }

    @NonNull
    @Override
    public FavoriteWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        FavoriteWordViewHolder favoriteWordViewHolder = new FavoriteWordViewHolder(v);
        return favoriteWordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteWordViewHolder holder, int position) {
        holder.tvWord.setText(mFavoriteWord.get(position).getWord());
        String meanInHtml = mFavoriteWord.get(position).getMean();
        String mean = meanInHtml.substring(meanInHtml.indexOf("<li>") + 4, meanInHtml.indexOf("</li>"));
        holder.tvMean.setText(Html.fromHtml(mean));
        holder.word = mFavoriteWord.get(position);
    }

    @Override
    public int getItemCount() {
        return mFavoriteWord.size();
    }

    public class FavoriteWordViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvWord;
        private TextView tvMean;
        private Word word;
        public FavoriteWordViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            this.tvMean = this.view.findViewById(R.id.tv_mean);
            this.tvWord = this.view.findViewById(R.id.tv_word);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("word",word);
                    bundle.putInt("kindWord",kindWord);
                    Navigation.findNavController(view).navigate(R.id.detail_word,bundle);
                }
            });
        }
    }
}
