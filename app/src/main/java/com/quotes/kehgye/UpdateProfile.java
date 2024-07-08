package com.quotes.kehgye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText userNameEditText, emailEditText, passwordEditText;
    private Button updateButton;
    private Uri profileImageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference profileImageRef;

    private String currentUserID;
    private Button updatepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        profileImageView = findViewById(R.id.updateProfileImageView);
        userNameEditText = findViewById(R.id.updateUserNameEditText);
        emailEditText = findViewById(R.id.updateEmailEditText);
        passwordEditText = findViewById(R.id.updatePasswordEditText);
        updateButton = findViewById(R.id.updateProfileButton);
        updatepic = findViewById(R.id.updateUploadPicButton);

        // Retrieve and display user data
        retrieveUserData();

        updatepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to pick image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            profileImageView.setImageURI(profileImageUri);
        }
    }

    private void retrieveUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();
                    String profilePictureUrl = dataSnapshot.child("profilePictureUrl").getValue().toString();

                    userNameEditText.setText(username);
                    emailEditText.setText(email);
                    passwordEditText.setText(password);
                    Picasso.get().load(profilePictureUrl).into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        final String username = userNameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (profileImageUri != null) {
            final StorageReference filePath = profileImageRef.child(currentUserID + ".jpg");
            filePath.putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    saveUserData(username, email, password, downloadUrl, progressDialog);
                                }
                            }
                        });
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(UpdateProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            saveUserData(username, email, password, null, progressDialog);
        }
    }

    private void saveUserData(String username, String email, String password, String profilePictureUrl, final ProgressDialog progressDialog) {
        userRef.child("username").setValue(username);
        userRef.child("email").setValue(email);
        userRef.child("password").setValue(password);

        if (profilePictureUrl != null) {
            userRef.child("profilePictureUrl").setValue(profilePictureUrl);
        }

        progressDialog.dismiss();
        Toast.makeText(UpdateProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
