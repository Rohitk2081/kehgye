package com.quotes.kehgye;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private List<Quote> quotesList;
    private Context context;

    public QuoteAdapter(List<Quote> quotesList, Context context) {
        this.quotesList = quotesList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotesList.get(position);

        holder.usernameTextView.setText(quote.getUsername());
        holder.captionTextView.setText(quote.getCaption());
        holder.likesTextView.setText(quote.getLikes() + " Likes");

        Glide.with(context).load(quote.getImageUrl()).into(holder.quoteImageView);

        holder.quoteImageView.setOnClickListener(v -> {
            if (!quote.isLiked()) {
                quote.setLiked(true);
                quote.setLikes(quote.getLikes() + 1);
                FirebaseFirestore.getInstance().collection("quotes").document(quote.getId())
                        .update("likes", quote.getLikes());
                holder.likesTextView.setText(quote.getLikes() + " Likes");
            }
        });

        // Set click listener on the username
        holder.usernameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("username", quote.getUsername());
            intent.putExtra("userId", quote.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quotesList.size();
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, captionTextView, likesTextView;
        ImageView quoteImageView;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            captionTextView = itemView.findViewById(R.id.captionTextView);
            likesTextView = itemView.findViewById(R.id.likesTextView);
            quoteImageView = itemView.findViewById(R.id.quoteImageView);
        }
    }
}
