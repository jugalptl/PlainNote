package com.example.plainolnotes;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EditorActivity extends ActionBarActivity {

    private String action;
    private EditText editor;
    private ImageView img_note;
    private GridView grid_images;
    private String oldText;
    private String noteFilter;
    private byte imageinbyte[];
    private byte oldimageinbyte[];
    private BroadcastReceiver broadcastReceiver;
    private String note_location;
    private ArrayList<Bitmap> images_note = new ArrayList<>();
    private ArrayList<Bitmap> old_images_note = new ArrayList<>();
    Uri uri;

    private ArrayList<byte[]> img_byte = new ArrayList<>();
    private ArrayList<byte[]> old_img_byte = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Geocoder geocoder = new Geocoder(EditorActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation((double) intent.getExtras().get("lat"), (double) intent.getExtras().get("long"), 1);
                        Address obj = addresses.get(0);
                        note_location = obj.getAddressLine(0) + obj.getAdminArea() + " , " + obj.getPostalCode();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("Note_Location  " + note_location);
                    System.out.println(intent.getExtras().get("lat") + " , " + intent.getExtras().get("long"));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("Location_Updates"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        stopService(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = (EditText) findViewById(R.id.editText);
        //img_note = (ImageView) findViewById(R.id.note_img);
        grid_images = (GridView) findViewById(R.id.gridview_img);


        Intent intent = getIntent();

        uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getApplicationContext().getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            System.out.println(cursor.toString());
            if (cursor != null) {
                cursor.moveToFirst();
                oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            }
                try {
                /*Fetching data from the second table images*/
                Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI1, DBOpenHelper.ALL_COLUMNS_IMAGES, noteFilter, null, null);
               // Cursor c= managedQuery(Uri.parse("content://"+NotesProvider.AUTHORITY),DBOpenHelper.ALL_COLUMNS_IMAGES,noteFilter,null,null);
                if(c!=null) {
                    c.moveToFirst();
                    do {
                        oldimageinbyte = c.getBlob(c.getColumnIndex(DBOpenHelper.NOTE_IMAGE));
                        System.out.println(c.getInt(c.getColumnIndex(DBOpenHelper.IMAGE_ID)));
                        System.out.println(c.getInt(c.getColumnIndex(DBOpenHelper.NOTE_ID)));
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        Bitmap oldimage = BitmapFactory.decodeByteArray(oldimageinbyte, 0, oldimageinbyte.length);
                        images_note.add(oldimage);

                    } while (c.moveToNext());
                }
                grid_images.setAdapter(new ImageViewAdapter(EditorActivity.this, images_note));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        editor.setText(oldText);
        editor.requestFocus();

        //Intent i = new Intent(getApplicationContext(),GPS_Service.class);
        //startService(i);

        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar snackbar = Snackbar
                        .make(v, "", Snackbar.LENGTH_LONG)
                        .setAction("Open Camera/Gallery to Add pictures", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditorActivity.this);
                                alertDialogBuilder.setMessage("Open Camera/Gallery")
                                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(i, 100);
                                            }
                                        })
                                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 200);
                                            Toast.makeText(getApplicationContext(),"Under Process",Toast.LENGTH_LONG).show();
                                            }
                                        }).show();


                            }
                        });

                snackbar.show();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //img_note.setImageBitmap(photo);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            imageinbyte = bytes.toByteArray();
            images_note.add(photo);
            img_byte.add(imageinbyte);

            //grid_images.setAdapter(new ImageViewAdapter(EditorActivity.this, images_note));
        }
         if(requestCode == 200 && resultCode == RESULT_OK)
        {
            Uri gallery_img_uri = data.getData();
            try {
                Bitmap gallery_img_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),gallery_img_uri);
                ByteArrayOutputStream gallery_img_bytes = new ByteArrayOutputStream();
                gallery_img_bitmap.compress(Bitmap.CompressFormat.JPEG,100,gallery_img_bytes);
                imageinbyte = gallery_img_bytes.toByteArray();
                img_byte.add(imageinbyte);
                images_note.add(gallery_img_bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        grid_images.setAdapter(new ImageViewAdapter(EditorActivity.this, images_note));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0 && img_byte.equals(null)) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText, img_byte);
                   // img_byte.clear();
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0 && oldimageinbyte.equals(null)) {
                    deleteNote();
                } else if (oldText.equals(newText) && img_byte.equals(null)) {
                    setResult(RESULT_CANCELED);
                    System.out.println("Canceled");
                } else {
                    updateNote(newText, img_byte);
                }
        }
        finish();
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        editor.setText("");
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateNote(String noteText, ArrayList<byte[]> img_byte) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        //values.put(DBOpenHelper.NOTE_IMAGE, imageinbyte);
         this.getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);


        ContentValues values1 = new ContentValues();
        for (int i = 0; i < img_byte.size(); i++) {
            values1.put(DBOpenHelper.NOTE_IMAGE, img_byte.get(i));
            values1.put(DBOpenHelper.NOTE_ID, uri.getLastPathSegment());
            values1.put(DBOpenHelper.IMAGE_LOCATION, "");
            this.getContentResolver().insert(NotesProvider.CONTENT_URI1, values1);

        }
        //Toast.makeText(this, R.string.note_update, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);


    }

    private void insertNote(String noteText, ArrayList<byte[]> images_note) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);

        Uri uri = getContentResolver().insert(NotesProvider.CONTENT_URI, values);

        Toast.makeText(this, uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
        ContentValues values1 = new ContentValues();
        for (int i = 0; i < images_note.size(); i++) {
            values1.put(DBOpenHelper.NOTE_IMAGE, images_note.get(i));
            values1.put(DBOpenHelper.NOTE_ID, uri.getLastPathSegment());
            values1.put(DBOpenHelper.IMAGE_LOCATION, "");
            getContentResolver().insert(NotesProvider.CONTENT_URI1, values1);

        }


        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
