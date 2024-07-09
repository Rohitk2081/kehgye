package com.quotes.kehgye;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {

    private Context context;
    private List<StorageReference> wallpaperRefs;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(StorageReference wallpaperRef);
    }

    public WallpaperAdapter(Context context, List<StorageReference> wallpaperRefs, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.wallpaperRefs = wallpaperRefs;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallpaper, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        StorageReference wallpaperRef = wallpaperRefs.get(position);
        wallpaperRef.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(context).load(uri).into(holder.ivWallpaperItem)
        );
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(wallpaperRef));
    }

    @Override
    public int getItemCount() {
        return wallpaperRefs.size();
    }

    public static class WallpaperViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWallpaperItem;

        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWallpaperItem = itemView.findViewById(R.id.ivWallpaperItem);
        }
    }
}
