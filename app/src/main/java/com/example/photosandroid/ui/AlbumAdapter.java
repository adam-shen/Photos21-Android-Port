package com.example.photosandroid.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid.AlbumActivity;
import com.example.photosandroid.R;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.MainActivity;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private ArrayList<Album> albums;
    private Context context;
    private OnAlbumLongClickListener longClickListener;

    public interface OnAlbumLongClickListener {
        void onAlbumLongClicked(Album album, int position);
    }


    public AlbumAdapter(ArrayList<Album> albums, Context context, OnAlbumLongClickListener longClickListener) {
        this.albums = albums;
        this.context = context;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.albumNameText.setText(album.getName());

        // Open AlbumActivity when an album is clicked
        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                Album currentAlbum = albums.get(currentPos);
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("albumName", currentAlbum.getName());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                Album currentAlbum = albums.get(currentPos);
                longClickListener.onAlbumLongClicked(currentAlbum, currentPos);
            }
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return albums.size();
    }

    // ViewHolder class
    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumNameText;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameText = itemView.findViewById(R.id.albumNameText);
        }
    }
}
