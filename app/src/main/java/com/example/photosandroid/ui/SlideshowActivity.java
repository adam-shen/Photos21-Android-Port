package com.example.photosandroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photosandroid.R;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Photo;
import com.example.photosandroid.model.Tag;
import com.example.photosandroid.utils.SerializationUtil;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class SlideshowActivity extends AppCompatActivity {

    private ArrayList<Photo> photos;
    private int currentIndex;
    private ImageView slideshowImage;
    private TextView captionView, tagListView;

    private static final String FILE_NAME = "albums.ser";
    private ArrayList<Album> albums;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Slideshow");
        }

        slideshowImage = findViewById(R.id.slideshowImage);
        captionView = findViewById(R.id.photoCaption);
        tagListView = findViewById(R.id.tagList);

        Intent intent = getIntent();
        String albumName = intent.getStringExtra("albumName");
        currentIndex = intent.getIntExtra("photoIndex", 0);

        albums = SerializationUtil.load(this, FILE_NAME);
        for (Album a : albums) {
            if (a.getName().equals(albumName)) {
                album = a;
                break;
            }
        }

        if (album == null || album.getPhotos().isEmpty()) {
            Toast.makeText(this, "Album not found or empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        photos = album.getPhotos();

        Button nextButton = findViewById(R.id.nextButton);
        Button prevButton = findViewById(R.id.prevButton);
        Button addTagButton = findViewById(R.id.addTagButton);
        Button deleteTagButton = findViewById(R.id.deleteTagButton);
        Button movePhotoButton = findViewById(R.id.movePhotoButton);


        nextButton.setOnClickListener(v -> {
            if (currentIndex < photos.size() - 1) {
                currentIndex++;
                showPhoto();
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showPhoto();
            }
        });

        addTagButton.setOnClickListener(v -> promptTagAction(true));
        deleteTagButton.setOnClickListener(v -> promptTagAction(false));

        movePhotoButton.setOnClickListener(v -> {
            ArrayList<String> albumNames = new ArrayList<>();
            for (Album a : albums) {
                if (!a.getName().equals(album.getName())) {
                    albumNames.add(a.getName());
                }
            }

            if (albumNames.isEmpty()) {
                Toast.makeText(this, "No other albums to move to", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] albumOptions = albumNames.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Move Photo To");
            builder.setItems(albumOptions, (dialog, which) -> {
                String targetAlbumName = albumOptions[which];
                Album targetAlbum = null;

                for (Album a : albums) {
                    if (a.getName().equals(targetAlbumName)) {
                        targetAlbum = a;
                        break;
                    }
                }

                if (targetAlbum == null) {
                    Toast.makeText(this, "Target album not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Photo photoToMove = photos.get(currentIndex);

                if (targetAlbum.getPhotos().contains(photoToMove)) {
                    Toast.makeText(this, "Photo already in target album", Toast.LENGTH_SHORT).show();
                    return;
                }

                targetAlbum.getPhotos().add(photoToMove);
                album.getPhotos().remove(currentIndex);
                SerializationUtil.save(albums, this, FILE_NAME);
                Toast.makeText(this, "Photo moved", Toast.LENGTH_SHORT).show();
                finish(); // Exit slideshow
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });


        showPhoto();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void showPhoto() {
        try {
            Photo photo = photos.get(currentIndex);
            Uri uri = Uri.parse(photo.getFilepath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            slideshowImage.setImageBitmap(bitmap);

            String caption = (photo.getCaption() == null || photo.getCaption().trim().isEmpty())
                    ? "No caption" : photo.getCaption();
            captionView.setText(caption);

            // Show tags
            Set<Tag> tags = photo.getTags();
            if (tags.isEmpty()) {
                tagListView.setText("Tags: (none)");
            } else {
                tagListView.setText("Tags:\n" + tags.stream()
                        .map(tag -> tag.getName() + ": " + tag.getValue())
                        .collect(Collectors.joining("\n")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            captionView.setText("Unable to load image");
            tagListView.setText("Tags: (error)");
            Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void promptTagAction(boolean adding) {
        if (adding) {
            String[] tagTypes = {"person", "location"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Tag");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, tagTypes);
            builder.setSingleChoiceItems(adapter, 0, null);

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Enter tag value");
            builder.setView(input);

            builder.setPositiveButton("Confirm", (dialog, which) -> {
                int selectedIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String type = tagTypes[selectedIndex];
                String value = input.getText().toString().trim();

                if (value.isEmpty()) {
                    Toast.makeText(this, "Tag value required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Tag tag = new Tag(type, value);
                Photo currentPhoto = photos.get(currentIndex);

                currentPhoto.addTag(tag);
                Toast.makeText(this, "Tag added", Toast.LENGTH_SHORT).show();

                SerializationUtil.save(albums, this, FILE_NAME);
                showPhoto();
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();

        } else {
            Photo currentPhoto = photos.get(currentIndex);
            ArrayList<Tag> tagList = new ArrayList<>(currentPhoto.getTags());

            if (tagList.isEmpty()) {
                Toast.makeText(this, "No tags to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] tagOptions = tagList.stream()
                    .map(tag -> tag.getName() + ": " + tag.getValue())
                    .toArray(String[]::new);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Tag");
            builder.setItems(tagOptions, (dialog, which) -> {
                Tag tagToRemove = tagList.get(which);
                currentPhoto.removeTag(tagToRemove);
                SerializationUtil.save(albums, this, FILE_NAME);
                Toast.makeText(this, "Tag deleted", Toast.LENGTH_SHORT).show();
                showPhoto();
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

}
