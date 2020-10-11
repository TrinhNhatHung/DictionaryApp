package com.example.dictionaryapp.databases;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;
import java.util.List;

public class DatabaseEngVieAccess {
   private SQLiteOpenHelper openHelper;
   private SQLiteDatabase database;
   private static DatabaseEngVieAccess instance;

   private DatabaseEngVieAccess(Context context) {
      this.openHelper = new DatabaseEngVieOpenHelper(context);
   }

   public static DatabaseEngVieAccess getInstance(Context context) {
      if (instance == null) {
         instance = new DatabaseEngVieAccess(context);
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
      Cursor cursor = database.rawQuery("SELECT id as _id, word as WORD, content as MEAN FROM anh_viet", null);
      cursor.moveToFirst();
      return cursor;
   }

   public Cursor getWordSearch(String filter) {
      Cursor cursor = database.rawQuery("SELECT id as _id,word as WORD, content as MEAN FROM anh_viet where word like '"+ filter +"%' limit 500", null);
      cursor.moveToPosition(-1);
      return cursor;
   }
     public List<Word> getFavoriteEnglishWords (){
         List<Word> result = new ArrayList<Word>();
         String sql = "SELECT favorite.id,word, content FROM favorite\n" +
                 "inner join anh_viet\n" +
                 "on anh_viet.id = favorite.id\n";
         Cursor cursor = database.rawQuery(sql,null);
         cursor.moveToFirst();
         while (!cursor.isAfterLast()){
            result.add(new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
         }
         cursor.close();
         return result;
     }


     public Word getOneWord (int id){
         String sql = "SELECT word,content FROM anh_viet WHERE id=" + id;
         Cursor cursor = database.rawQuery(sql,null);
         cursor.moveToFirst();
         Word word = new Word(cursor.getString(0), cursor.getString(1));
         cursor.close();
         return word;
     }

     public int getRowCount (){
       String sql ="SELECT count(id) FROM anh_viet";
       Cursor cursor = database.rawQuery(sql,null);
       cursor.moveToFirst();
       int rowCount = cursor.getInt(0);
       cursor.close();
       return rowCount;
     }

     public boolean checkFavorite (int id){
        String sql = "SELECT count(id) FROM favorite WHERE id =" + id;
        Cursor cursor = database.rawQuery(sql,null);
        cursor.moveToFirst();
        int countRow = cursor.getInt(0);
        return countRow > 0 ;
     }

     public void insertFavoriteWord (int id){
         String sql = "INSERT INTO favorite (id) values ('" + id + "')";
         database.execSQL(sql);
     }

     public void deleteFavoriteWord (int id){
         String sql = "DELETE FROM favorite where id=" + id;
         database.execSQL(sql);
     }

}
