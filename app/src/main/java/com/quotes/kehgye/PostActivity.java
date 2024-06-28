package com.quotes.kehgye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    private ImageView ivPostImage;
    private EditText etCaption;
    private Button btnPublish;

    private File imageFile;
    private String quoteText;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ivPostImage = findViewById(R.id.ivPostImage);
        etCaption = findViewById(R.id.etCaption);
        btnPublish = findViewById(R.id.btnPublish);

        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("IMAGE_FILE_PATH");
        quoteText = intent.getStringExtra("QUOTE_TEXT");

        if (imagePath != null && quoteText != null) {
            imageFile = new File(imagePath);
            ivPostImage.setImageURI(Uri.fromFile(imageFile));
            etCaption.setText(quoteText);
        }

        btnPublish.setOnClickListener(v -> {
            if (imageFile != null && !etCaption.getText().toString().isEmpty()) {
                uploadImageAndQuote(imageFile, etCaption.getText().toString());
                Intent intent1 = new Intent(PostActivity.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            } else {
                Toast.makeText(PostActivity.this, "Please enter a caption and select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageAndQuote(File imageFile, String caption) {
        Uri fileUri = Uri.fromFile(imageFile);
        StorageReference storageRef = firebaseStorage.getReference().child("quotes_images/" + imageFile.getName());

        storageRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveQuoteWithImageUrl(quoteText, imageUrl, caption);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                Toast.makeText(PostActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Upload failed: " + e.getMessage());
            Toast.makeText(PostActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveQuoteWithImageUrl(String text, String imageUrl, String caption) {
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Fetch username from Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);

                    // Create Quote object with fetched username
                    Quote quote = new Quote(text, imageUrl, caption, 0, userId, username); // Initializing likes to 0

                    // Save quote to Firestore
                    firestore.collection("quotes").add(quote)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(PostActivity.this, "Quote posted successfully!", Toast.LENGTH_SHORT).show();
                                // Finish the activity after successful post
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error posting quote: " + e.getMessage());
                                Toast.makeText(PostActivity.this, "Error posting quote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e(TAG, "User data not found for userId: " + userId);
                    Toast.makeText(PostActivity.this, "User data not found. Unable to post quote.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(PostActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
