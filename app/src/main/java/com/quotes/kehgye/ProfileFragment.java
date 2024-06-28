package com.quotes.kehgye;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profilePhoto;
    private TextView usernameTextView, emailTextView, postsCountTextView;
    private RecyclerView recyclerViewPosts;
    private List<Quote> quotesList;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Assuming your users node in Realtime Database

        // Get current user ID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // Initialize quotes list
        quotesList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profilePhoto = view.findViewById(R.id.imageViewProfile);
        usernameTextView = view.findViewById(R.id.textViewUsername);
        emailTextView = view.findViewById(R.id.textViewEmail);
        postsCountTextView = view.findViewById(R.id.posts_count);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new GridLayoutManager(requireContext(), 3)); // Example: Grid with 3 columns

        // Load user profile data
        loadUserProfile();

        // Retrieve user's posts
        retrieveUserPosts();

        return view;
    }

    private void loadUserProfile() {
        // Retrieve user data from Realtime Database
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        usernameTextView.setText(user.getUsername());
                        emailTextView.setText(user.getEmail());

                        // Load profile photo using Glide
                        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                            Glide.with(requireContext())
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
                Log.e("ProfileFragment", "Error retrieving user profile: " + error.getMessage());
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveUserPosts() {
        firestore.collection("quotes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    quotesList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Create a Quote object with just the imageUrl
                            Quote quote = new Quote(null, imageUrl, null, 0, null, null);
                            quotesList.add(quote);
                        }
                    }

                    // Update UI with retrieved posts
                    PostsAdapter adapter = new PostsAdapter(quotesList);
                    recyclerViewPosts.setAdapter(adapter);

                    // Update posts count
                    postsCountTextView.setText(String.valueOf(quotesList.size()));
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error retrieving user posts: " + e.getMessage());
                });
    }

}