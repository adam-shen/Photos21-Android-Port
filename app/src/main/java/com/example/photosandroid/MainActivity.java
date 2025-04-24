package com.example.photosandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid.model.Album;
import com.example.photosandroid.ui.AlbumAdapter;
import com.example.photosandroid.utils.SerializationUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Album> albums;
    private RecyclerView albumRecyclerView;
    private AlbumAdapter albumAdapter;
    private static final String FILE_NAME = "albums.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumRecyclerView = findViewById(R.id.albumRecyclerView);
        albumRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        albums = SerializationUtil.load(this, FILE_NAME);
        if (albums == null) {
            albums = new ArrayList<>();
        }

        albumAdapter = new AlbumAdapter(albums, this);
        albumRecyclerView.setAdapter(albumAdapter);

        Button addAlbumButton = findViewById(R.id.addAlbumButton);
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album newAlbum = new Album("New Album " + (albums.size() + 1));
                albums.add(newAlbum);
                albumAdapter.notifyItemInserted(albums.size() - 1);
                SerializationUtil.save(albums, MainActivity.this, FILE_NAME);
            }
        });
    }
}

//Home screen: show album list