package com.quotes.kehgye;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class CreateFragment extends Fragment {

    private EditText etQuote;
    private ImageView ivDesign;
    private TextView tvCharCount;

    private static final int MAX_CHAR_COUNT = 4000;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        etQuote = view.findViewById(R.id.etQuote);
        ivDesign = view.findViewById(R.id.ivDesign);
        tvCharCount = view.findViewById(R.id.tvCharCount);

        // Initialize the character count
        tvCharCount.setText(String.valueOf(MAX_CHAR_COUNT));

        // Set up the toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set up the design icon click listener
        ivDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quoteText = etQuote.getText().toString();
                Intent intent = new Intent(getActivity(), DesignActivity.class);
                intent.putExtra("QUOTE_TEXT", quoteText);
                startActivity(intent);
            }
        });

        // Set up the text change listener for the EditText
        etQuote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the character count
                int remainingChars = MAX_CHAR_COUNT - s.length();
                tvCharCount.setText(String.valueOf(remainingChars));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        return view;
    }
}
