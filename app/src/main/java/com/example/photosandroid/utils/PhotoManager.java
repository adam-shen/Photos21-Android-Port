package com.example.photosandroid.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosandroid.R;
import com.example.photosandroid.model.Photo;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PhotoManager extends RecyclerView.Adapter<PhotoManager.PhotoViewHolder> {

    private ArrayList<Photo> photos = new ArrayList<>();

    public PhotoManager(ArrayList<Photo> photos) {
        this.photos = photos;
    }


    public  void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public  void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public  void clearPhotos() {
        photos.clear();
    }

    public  ArrayList<Photo> getPhotos() {
        return photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);

        String displayCaption = photo.getCaption();
        if (displayCaption == null || displayCaption.trim().isEmpty()) {
            String[] parts = photo.getFilepath().split("/");
            displayCaption = parts[parts.length - 1];
        }

        String formattedDate = "Unknown Date";
        if (photo.getDateTaken() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            formattedDate = photo.getDateTaken().format(formatter);
        }

        holder.photoCaptionText.setText(displayCaption);
        holder.photoDateText.setText(formattedDate);
    }



    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView photoCaptionText;
        TextView photoDateText;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoCaptionText = itemView.findViewById(R.id.photoCaptionText);
            photoDateText = itemView.findViewById(R.id.photoDateText);
        }
    }
}
