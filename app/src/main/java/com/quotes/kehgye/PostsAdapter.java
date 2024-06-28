package com.quotes.kehgye;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private List<Quote> quotesList;

    // Constructor
    public PostsAdapter(List<Quote> quotesList) {
        this.quotesList = quotesList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Quote quote = quotesList.get(position);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(quote.getImageUrl())
                .placeholder(R.drawable.ic_bold)
                .error(R.drawable.ic_italic)
                .into(holder.imageViewPost);
    }

    @Override
    public int getItemCount() {
        return quotesList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPost = itemView.findViewById(R.id.postImageView);
        }
    }
}
