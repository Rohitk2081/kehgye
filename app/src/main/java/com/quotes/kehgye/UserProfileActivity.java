package com.quotes.kehgye;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profilePhoto;
    private TextView usernameTextView, emailTextView, postsCountTextView, followersCountTextView, followingCountTextView;
    private RecyclerView recyclerViewPosts;
    private List<Quote> quotesList;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // Updated layout resource

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Get userId and username from Intent extras
        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");

        // Initialize views
        profilePhoto = findViewById(R.id.imageViewProfile_alt);
        usernameTextView = findViewById(R.id.textViewUsername_alt);
        emailTextView = findViewById(R.id.textViewEmail_alt);
        postsCountTextView = findViewById(R.id.posts_count_alt);
        followersCountTextView = findViewById(R.id.followers_count_alt);
        followingCountTextView = findViewById(R.id.following_count_alt);
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts_alt);
        recyclerViewPosts.setLayoutManager(new GridLayoutManager(this, 3));

        // Initialize quotes list
        quotesList = new ArrayList<>();

        // Load user profile data
        loadUserProfile();

        // Retrieve user's posts
        retrieveUserPosts();
    }

    private void loadUserProfile() {
        if (userId != null) {
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            usernameTextView.setText(user.getUsername());
                            emailTextView.setText(user.getEmail());

                            if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                                Glide.with(UserProfileActivity.this)
                                        .load(user.getProfilePictureUrl())
                                        .placeholder(R.drawable.ic_bold)
                                        .error(R.drawable.ic_italic)
                                        .into(profilePhoto);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserProfileActivity", "Error retrieving user profile: " + error.getMessage());
                    Toast.makeText(UserProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void retrieveUserPosts() {
        if (userId != null) {
            firestore.collection("quotes")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        quotesList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String imageUrl = documentSnapshot.getString("imageUrl");

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Quote quote = new Quote(null, imageUrl, null, 0, null, null);
                                quotesList.add(quote);
                            }
                        }

                        PostsAdapter adapter = new PostsAdapter(quotesList);
                        recyclerViewPosts.setAdapter(adapter);
                        postsCountTextView.setText(String.valueOf(quotesList.size()));
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserProfileActivity", "Error retrieving user posts: " + e.getMessage());
                    });
        }
    }
}
