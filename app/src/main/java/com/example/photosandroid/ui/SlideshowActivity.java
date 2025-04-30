package com.example.photosandroid.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photosandroid.R;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Photo;
import com.example.photosandroid.utils.SerializationUtil;

import java.util.ArrayList;

public class SlideshowActivity extends AppCompatActivity {

    private ArrayList<Photo> photos;
    private int currentIndex;
    private ImageView slideshowImage;
    private TextView captionView;

    private static final String FILE_NAME = "albums.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        slideshowImage = findViewById(R.id.slideshowImage);
        captionView = findViewById(R.id.photoCaption);

        Intent intent = getIntent();
        String albumName = intent.getStringExtra("albumName");
        currentIndex = intent.getIntExtra("photoIndex", 0);

        ArrayList<Album> albums = SerializationUtil.load(this, FILE_NAME);
        Album album = null;
        for (Album a : albums) {
            if (a.getName().equals(albumName)) {
                album = a;
                break;
            }
        }

        if (album == null) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        photos = album.getPhotos();
        if (photos.isEmpty()) {
            Toast.makeText(this, "No photos in album", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button nextButton = findViewById(R.id.nextButton);
        Button prevButton = findViewById(R.id.prevButton);

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

        showPhoto();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

    }

    private void showPhoto() {
        try {
            Photo photo = photos.get(currentIndex);
            Uri uri = Uri.parse(photo.getFilepath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            slideshowImage.setImageBitmap(bitmap);

            String caption = (photo.getCaption() == null || photo.getCaption().isEmpty())
                    ? "Untitled" : photo.getCaption();
            captionView.setText(caption);

        } catch (Exception e) {
            e.printStackTrace();
            captionView.setText("Unable to load image");
            Toast.makeText(this, "Failed to load image. It may have been moved or deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
