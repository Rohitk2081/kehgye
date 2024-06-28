// HomeFragment.java
package com.quotes.kehgye;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView recyclerView;
    private QuoteAdapter quoteAdapter;
    private List<Quote> quotesList;

    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        recyclerView = view.findViewById(R.id.recyclerViewQuotes);

        tvTitle.setText("Kehgye");

        quotesList = new ArrayList<>();
        quoteAdapter = new QuoteAdapter(quotesList, getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(quoteAdapter);

        firestore = FirebaseFirestore.getInstance();

        fetchQuotes();

        return view;
    }

    private void fetchQuotes() {
        firestore.collection("quotes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Quote quote = document.toObject(Quote.class);
                            quote.setId(document.getId()); // Set the document ID
                            quotesList.add(quote);
                        }
                        quoteAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
