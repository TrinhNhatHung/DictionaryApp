package com.example.dictionaryapp.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;
import java.util.List;

public class DatabaseVieEngAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseVieEngAccess instance;

    private DatabaseVieEngAccess(Context context) {
        this.openHelper = new DatabaseVieEngOpenHelper(context);
    }

    public static DatabaseVieEngAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseVieEngAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor getWords(){
        Cursor cursor = database.rawQuery("SELECT id as _id, word as WORD , content as MEAN FROM viet_anh", null);
        cursor.moveToFirst();
        return cursor;
    }

    public List<Word> getFavoriteVietnameseWords (){
        List<Word> result = new ArrayList<Word>();
        String sql = "SELECT favorite.id,word, content FROM favorite\n" +
                "inner join viet_anh\n" +
                "on viet_anh.id = favorite.id\n";
        Cursor cursor = database.rawQuery(sql,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result.add(new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public Cursor getWordSearch(String filter) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT id as _id,word as WORD, content as MEAN FROM viet_anh where word like '"+ filter +"%' limit 500", null);
        cursor.moveToPosition(-1);
        return cursor;
    }

    public boolean checkFavorite (int id){
        String sql = "SELECT count(id) FROM favorite WHERE id =" + id;
        Cursor cursor = database.rawQuery(sql,null);
        cursor.moveToFirst();
        int countRow = cursor.getInt(0);
        return countRow > 0 ;
    }

    public void insertFavoriteWord (int id){
        String sql = "INSERT INTO favorite (id) VALUES ('" + id + "')";
        database.execSQL(sql);
    }
    public void deleteFavorite (int id){
        String sql = "DELETE FROM favorite WHERE id =" + id;
        database.execSQL(sql);
    }

}
