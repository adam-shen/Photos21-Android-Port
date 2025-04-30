package com.example.photosandroid.utils;

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
import com.example.photosandroid.ui.SlideshowActivity;
import java.util.ArrayList;

public class PhotoManager extends RecyclerView.Adapter<PhotoManager.PhotoViewHolder> {

    private ArrayList<Photo> photos;
    private String albumName;
    private PhotoClickListener photoClickListener;

    public interface PhotoClickListener {
        void onPhotoLongClicked(int position);
    }

    public PhotoManager(ArrayList<Photo> photos, String albumName) {
        this.photos = photos;
        this.albumName = albumName;
    }

    public void setPhotoClickListener(PhotoClickListener listener) {
        this.photoClickListener = listener;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(int index) {
        photos.remove(index);
    }

    public ArrayList<Photo> getPhotos() {
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

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(v.getContext(), SlideshowActivity.class);
                intent.putExtra("photoIndex", currentPos);
                intent.putExtra("albumName", albumName);
                v.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && photoClickListener != null) {
                photoClickListener.onPhotoLongClicked(currentPos);
            }
            return true;
        });

        try {
            Uri uri = Uri.parse(photo.getFilepath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(holder.itemView.getContext().getContentResolver(), uri);
            holder.photoThumbnail.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            holder.photoThumbnail.setImageResource(android.R.drawable.ic_menu_report_image);
            Toast.makeText(holder.itemView.getContext(), "Image file not found", Toast.LENGTH_SHORT).show();
        }
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
