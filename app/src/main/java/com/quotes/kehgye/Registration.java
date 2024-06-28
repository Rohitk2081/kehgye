package com.quotes.kehgye;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class Registration extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private EditText userNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton, uploadPicButton;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userNameEditText = findViewById(R.id.userNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        uploadPicButton = findViewById(R.id.uploadPicButton);
        profileImageView = findViewById(R.id.profileImageView);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        uploadPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        requestPermissions();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_PERMISSION_CODE);
            }
        }
    }

    private void chooseProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture")
                .setItems(new CharSequence[]{"Choose from Gallery", "Take a Photo"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                                break;
                            case 1:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                                break;
                        }
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            profileImageView.setImageURI(profileImageUri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImageUri = getImageUriFromBitmap(imageBitmap);
            profileImageView.setImageBitmap(imageBitmap);
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProfilePicture", null);
        return Uri.parse(path);
    }

    private void registerUser() {
        final String username = userNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(Registration.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(Registration.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    if (profileImageUri != null) {
                        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userId + ".jpg");
                        profileImageRef.putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String profilePictureUrl = uri.toString();
                                            saveUserToDatabase(userId, username, email, password, profilePictureUrl); // Pass password to the method
                                        }
                                    });
                                } else {
                                    Toast.makeText(Registration.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        saveUserToDatabase(userId, username, email, password, "default"); // Pass password to the method
                    }
                } else {
                    Toast.makeText(Registration.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserToDatabase(String userId, String username, String email, String password, String profilePictureUrl) {
        User user = new User(userId, username, email, password, profilePictureUrl); // Include password in User constructor
        mDatabase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            // Redirect to login or main activity
                } else {
                    Toast.makeText(Registration.this, "Failed to register. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
