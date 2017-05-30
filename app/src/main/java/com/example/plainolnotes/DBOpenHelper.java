package com.example.plainolnotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    //Constants for db name and version
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_IMAGES = "images";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_IMAGE = "noteImage";
    public static final String NOTE_CREATED = "noteCreated";

    public static final String IMAGE_ID = "image_id";
    public static final String IMAGE_LOCATION = "location";

    public static final String[] ALL_COLUMNS =
            {NOTE_ID, NOTE_TEXT, NOTE_CREATED};

    public static final String[] ALL_COLUMNS_IMAGES = {IMAGE_ID,NOTE_IMAGE,IMAGE_LOCATION,NOTE_ID};

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    private static final String IMAGE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_IMAGES + " (" +
                    IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_IMAGE + " BLOB, " +
                    IMAGE_LOCATION + " TEXT, " +
                    NOTE_ID+ " INTEGER, "+
                    " FOREIGN KEY ("+NOTE_ID+") REFERENCES "+TABLE_NOTES+"("+NOTE_ID+")"+
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(IMAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_IMAGES);
        onCreate(db);
    }
}
