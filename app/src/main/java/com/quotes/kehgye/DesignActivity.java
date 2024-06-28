package com.quotes.kehgye;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DesignActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivPost;
    private ImageView ivWallpaper;
    private TextView tvQuote;
    private LinearLayout toolLayout;
    private SeekBar seekBarTextSize;
    private ImageView ivAttachment;
    private ImageView ivBold, ivItalic, ivTextColor;
    private ImageView ivAlignLeft, ivAlignCenter, ivAlignRight, ivAlignJustify;

    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isTextColorSelected = false;

    private float finalX, finalY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ivPost = findViewById(R.id.ivPost);
        ivWallpaper = findViewById(R.id.ivWallpaper);
        tvQuote = findViewById(R.id.tvQuote);
        toolLayout = findViewById(R.id.toolLayout);
        seekBarTextSize = findViewById(R.id.seekBarTextSize);
        ivAttachment = findViewById(R.id.ivAttachments);
        ivBold = findViewById(R.id.ivBold);
        ivItalic = findViewById(R.id.ivItalic);
        ivTextColor = findViewById(R.id.ivTextColor);
        ivAlignLeft = findViewById(R.id.ivAlignLeft);
        ivAlignCenter = findViewById(R.id.ivAlignCenter);
        ivAlignRight = findViewById(R.id.ivAlignRight);
        ivAlignJustify = findViewById(R.id.ivAlignJustify);

        // Get the quote text from the intent
        String quoteText = getIntent().getStringExtra("QUOTE_TEXT");
        if (quoteText != null) {
            tvQuote.setText(quoteText);
        }

        // Set up the post icon click listener
        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap combinedImage = combineImageAndText();
                File imageFile = saveCombinedImageAsFile(combinedImage);

                Intent intent = new Intent(DesignActivity.this, PostActivity.class);
                intent.putExtra("IMAGE_FILE_PATH", imageFile.getAbsolutePath());
                intent.putExtra("QUOTE_TEXT", tvQuote.getText().toString());
                intent.putExtra("TEXTVIEW_FINAL_X", finalX);
                intent.putExtra("TEXTVIEW_FINAL_Y", finalY);
                startActivity(intent);
            }
        });

        // Set up the touch listener to move the quote
        tvQuote.setOnTouchListener(new View.OnTouchListener() {
            private float dx, dy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = v.getX() - event.getRawX();
                        dy = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .x(event.getRawX() + dx)
                                .y(event.getRawY() + dy)
                                .setDuration(0)
                                .start();

                        // Update final coordinates
                        finalX = event.getRawX() + dx;
                        finalY = event.getRawY() + dy;
                        break;
                }
                return true;
            }
        });

        // Set up the seek bar listener for text size adjustment
        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvQuote.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set up the attachment icon click listener to pick an image
        ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // Set up text alignment icons click listeners
        ivAlignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQuote.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
        });

        ivAlignCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQuote.setGravity(Gravity.CENTER);
            }
        });

        ivAlignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQuote.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            }
        });

        ivAlignJustify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQuote.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL);
            }
        });

        // Set up bold icon click listener
        ivBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBold = !isBold;
                updateTextStyle();
            }
        });

        // Set up italic icon click listener
        ivItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isItalic = !isItalic;
                updateTextStyle();
            }
        });

        // Set up text color icon click listener
        ivTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTextColorSelected = !isTextColorSelected;
                updateTextStyle();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivWallpaper.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTextStyle() {
        int style = 0;
        if (isBold) {
            style += Typeface.BOLD;
            ivBold.setImageResource(R.drawable.ic_align_center); // Example of highlighted icon
        } else {
            ivBold.setImageResource(R.drawable.ic_bold); // Example of normal icon
        }
        if (isItalic) {
            style += Typeface.ITALIC;
            ivItalic.setImageResource(R.drawable.ic_align_center); // Example of highlighted icon
        } else {
            ivItalic.setImageResource(R.drawable.ic_italic); // Example of normal icon
        }

        // Apply text color change if selected
        if (isTextColorSelected) {
            tvQuote.setTextColor(getResources().getColor(R.color.white)); // Example of selected color
            ivTextColor.setImageResource(R.drawable.ic_align_center); // Example of highlighted icon
        } else {
            tvQuote.setTextColor(getResources().getColor(R.color.black)); // Example of default color
            ivTextColor.setImageResource(R.drawable.ic_text_color); // Example of normal icon
        }

        // Apply combined styles to text view
        tvQuote.setTypeface(null, style);
    }

    private Bitmap combineImageAndText() {
        Bitmap bitmap = Bitmap.createBitmap(ivWallpaper.getWidth(), ivWallpaper.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ivWallpaper.draw(canvas);
        tvQuote.draw(canvas);
        return bitmap;
    }

    private File saveCombinedImageAsFile(Bitmap bitmap) {
        File imageFile = null;
        try {
            File storageDir = getCacheDir(); // Use cache directory instead of external storage
            String imageFileName = "QUOTE_" + System.currentTimeMillis() + ".jpg";
            imageFile = new File(storageDir, imageFileName);

            FileOutputStream fOut = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }
}