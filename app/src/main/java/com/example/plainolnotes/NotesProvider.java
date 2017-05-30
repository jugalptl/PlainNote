package com.example.plainolnotes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NotesProvider extends ContentProvider{

     static final String AUTHORITY = "com.example.plainolnotes.notesprovider";
     static final String BASE_PATH = "notes";
     static final String BASE_PATH1 = "images";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );
    public static final Uri CONTENT_URI1 =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH1 );

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", NOTES_ID);

    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }


         if(uri.equals(NotesProvider.CONTENT_URI1))
        {
            cursor= database.query(DBOpenHelper.TABLE_IMAGES,DBOpenHelper.ALL_COLUMNS_IMAGES,selection,null,null,null,null);

        }
         else if(uri.equals(NotesProvider.CONTENT_URI))
         {
            cursor= database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
                     selection, null, null, null,
                     DBOpenHelper.NOTE_CREATED + " DESC");
         }
        else {//(uri.equals(NotesProvider.CONTENT_URI+"/"+uri.getLastPathSegment())){
             cursor = database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
                     selection, null, null, null,null);
         }

       /* return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
                selection, null, null, null,
                DBOpenHelper.NOTE_CREATED + " DESC");*/
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        if(uri.equals(NotesProvider.CONTENT_URI)) {
             id = database.insert(DBOpenHelper.TABLE_NOTES,
                    null, values);
        }
        else if(uri.equals(NotesProvider.CONTENT_URI1))
        {
            id = database.insert(DBOpenHelper.TABLE_IMAGES,
                    null, values);
        }
        //database.rawQuery("INSERT INTO "+DBOpenHelper.TABLE_IMAGES+"("+DBOpenHelper.NOTE_IMAGE+","+DBOpenHelper.IMAGE_LOCATION+","+DBOpenHelper.NOTE_ID+") VALUES")
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int id =0;
    if(uri.equals(NotesProvider.CONTENT_URI1))
    {
       id = database.update(DBOpenHelper.TABLE_IMAGES,values,selection,selectionArgs);
    }
        else if(uri.equals(NotesProvider.CONTENT_URI))
    {
       id =  database.update(DBOpenHelper.TABLE_NOTES,
                values, selection, selectionArgs);
    }
        return id;
    }
}
