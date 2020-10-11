package com.example.dictionaryapp.databases;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseEngVieOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "anh_viet.db";
    private static final int DATABASE_VERSION = 1;
    public DatabaseEngVieOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
