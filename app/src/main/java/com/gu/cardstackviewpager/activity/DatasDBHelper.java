package com.gu.cardstackviewpager.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatasDBHelper extends SQLiteOpenHelper {
    private static final String CREATE_WANT = "create table Want(" + "id integer , " + "numWant integer primary key )";
    //private static final String CREATE_WATCHED = "create table Watched(" + "id integer , " + "numWatched integer  primary key)";
   // private static final String CREATE_HREF = "create table Href(" + "id integer , " + "href string  primary key)";

    public DatasDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //执行建表语句
        db.execSQL(CREATE_WANT);
        //db.execSQL(CREATE_WATCHED);
        //db.execSQL(CREATE_HREF);
        Log.d("xfhy","建表成功!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
