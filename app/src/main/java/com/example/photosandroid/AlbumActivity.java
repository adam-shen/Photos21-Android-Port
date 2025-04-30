package com.example.photosandroid;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Photo;
import com.example.photosandroid.utils.PhotoManager;
import com.example.photosandroid.utils.SerializationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    private RecyclerView photoRecyclerView;
    private PhotoManager photoManager;
    private ArrayList<Photo> photos;
    private Album album;
    private ArrayList<Album> albums;
    private static final String FILE_NAME = "albums.ser"; // Same file as MainActivity
    private String albumName; // To identify which album

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        albumName = getIntent().getStringExtra("albumName");

        TextView albumTitle = findViewById(R.id.albumTitle);
        albumTitle.setText(albumName);

        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        albums = SerializationUtil.load(this, FILE_NAME);
        if (albums == null) {
            albums = new ArrayList<>();
        }

        // Find the selected album
        album = null;
        for (Album a : albums) {
            if (a.getName().equals(albumName)) {
                album = a;
                break;
            }
        }

        if (album == null) {
            album = new Album(albumName);
            albums.add(album);
            SerializationUtil.save(albums, this, FILE_NAME);
        }

        photos = album.getPhotos();
        photoManager = new PhotoManager(photos, albumName);
        photoRecyclerView.setAdapter(photoManager);
        photoManager.notifyDataSetChanged();

        //  Add delete-on-long-press functionality
        photoManager.setPhotoClickListener(position -> {
            new AlertDialog.Builder(AlbumActivity.this)
                    .setTitle("Delete Photo")
                    .setMessage("Are you sure you want to delete this photo?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        album.getPhotos().remove(position);
                        SerializationUtil.save(albums, AlbumActivity.this, FILE_NAME);
                        photoManager.notifyItemRemoved(position);
                        Toast.makeText(AlbumActivity.this, "Photo deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        Button addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Extract file name as caption
            String filename = "Untitled";
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    filename = cursor.getString(nameIndex);
                }
                cursor.close();
            }

            Photo newPhoto = new Photo(uri.toString(), filename, LocalDateTime.now());
            album.getPhotos().add(newPhoto);
            SerializationUtil.save(albums, this, FILE_NAME);
            photoManager.notifyItemInserted(album.getPhotos().size() - 1);
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Finish AlbumActivity and go back to MainActivity
        return true;
    }

}
