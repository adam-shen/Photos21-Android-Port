package com.example.photosandroid.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid.R;
import com.example.photosandroid.model.Photo;

import java.util.ArrayList;
import java.util.Map;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.PhotoViewHolder> {

    private final ArrayList<Photo> photos;
    private final Map<Photo, String> albumMap; // Maps each photo to its album
    private final Context context;

    public SearchResultAdapter(Context context, ArrayList<Photo> photos, Map<Photo, String> albumMap) {
        this.context = context;
        this.photos = photos;
        this.albumMap = albumMap;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);

        try {
            Uri uri = Uri.parse(photo.getFilepath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            holder.photoThumbnail.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.photoThumbnail.setImageResource(android.R.drawable.ic_menu_report_image);
            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
        }

        holder.itemView.setOnClickListener(v -> {
            String albumName = albumMap.get(photo);
            if (albumName == null) {
                Toast.makeText(context, "Album not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, SlideshowActivity.class);
            intent.putExtra("albumName", albumName);

            // Find index of photo in its album
            int photoIndex = -1;
            ArrayList<com.example.photosandroid.model.Album> allAlbums =
                    com.example.photosandroid.utils.SerializationUtil.load(context, "albums.ser");

            for (com.example.photosandroid.model.Album album : allAlbums) {
                if (album.getName().equals(albumName)) {
                    photoIndex = album.getPhotos().indexOf(photo);
                    break;
                }
            }

            if (photoIndex != -1) {
                intent.putExtra("photoIndex", photoIndex);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Photo not found in album", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoThumbnail;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoThumbnail = itemView.findViewById(R.id.photoThumbnail);
        }
    }
}

