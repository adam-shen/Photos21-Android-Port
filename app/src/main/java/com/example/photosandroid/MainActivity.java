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

        albumAdapter = new AlbumAdapter(albums, this, new AlbumAdapter.OnAlbumLongClickListener() {
            @Override
            public void onAlbumLongClicked(Album album, int position) {
                showAlbumOptionsDialog(album, position);
            }
        });
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

    private void showAlbumOptionsDialog(Album album, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Options for " + album.getName())
                .setItems(new String[]{"Rename Album", "Delete Album"}, (dialog, which) -> {
                    if (which == 0) {
                        showRenameAlbumDialog(album, position);
                    } else if (which == 1) {
                        showDeleteAlbumDialog(album, position);
                    }
                })
                .show();
    }

    private void showRenameAlbumDialog(Album album, int position) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(album.getName());
        input.setSelection(input.getText().length()); // move cursor to end

        new android.app.AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setView(input)
                .setPositiveButton("Rename", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        android.widget.Toast.makeText(this, "Name cannot be empty", android.widget.Toast.LENGTH_SHORT).show();
                    } else if (isDuplicateAlbumName(newName)) {
                        android.widget.Toast.makeText(this, "Album name already exists", android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        album.renameAlbum(newName);
                        SerializationUtil.save(albums, this, FILE_NAME);
                        albumAdapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAlbumDialog(Album album, int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Album")
                .setMessage("Are you sure you want to delete \"" + album.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    albums.remove(position);
                    SerializationUtil.save(albums, this, FILE_NAME);
                    albumAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean isDuplicateAlbumName(String name) {
        for (Album a : albums) {
            if (a.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}

//Home screen: show album list