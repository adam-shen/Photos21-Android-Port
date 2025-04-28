package com.example.photosandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumName = getIntent().getStringExtra("albumName");

        TextView albumTitle = findViewById(R.id.albumTitle);
        albumTitle.setText(albumName);

        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
            album = new Album(albumName); // Create if not found
            albums.add(album);
            SerializationUtil.save(albums, this, FILE_NAME);
        }

        photos = album.getPhotos(); // Get the photos list
        photoManager = new PhotoManager(photos);
        photoRecyclerView.setAdapter(photoManager);
        photoManager.notifyDataSetChanged();

        Button addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add an empty photo properly
                Photo newPhoto = new Photo("", "", LocalDateTime.now());
                photoManager.addPhoto(newPhoto);
                photoManager.notifyItemInserted(photoManager.getPhotos().size() - 1);

                SerializationUtil.save(albums, AlbumActivity.this, FILE_NAME);
            }
        });
    }
}
