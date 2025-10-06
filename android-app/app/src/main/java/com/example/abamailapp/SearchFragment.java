package com.example.abamailapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abamailapp.adapters.MailListAdapter;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.User;
import com.example.abamailapp.repositories.UserRepository;
import com.example.abamailapp.viewmodels.MailViewModel;

import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private MailViewModel mailViewModel;
    private MailListAdapter adapter;
    private RecyclerView recyclerView;
    private AppDB db;
    private UserRepository userRepository;

    private static final String ARG_QUERY = "query";

    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Title
        TextView folderTitle = view.findViewById(R.id.folderTitle);
        folderTitle.setText("Search Results");

        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MailListAdapter();
        recyclerView.setAdapter(adapter);

        mailViewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);

        db = DatabaseClient.getInstance(getContext());
        int userId = SessionManager.getLoggedInUserId();

        TextView noResultsText = view.findViewById(R.id.no_results_text);
        String query;
        if (getArguments() != null) {
            query = getArguments().getString(ARG_QUERY, "");
        } else {
            query = "";
        }

        LiveData<List<Mail>> searchResult = mailViewModel.searchMails(query);
        searchResult.removeObservers(getViewLifecycleOwner()); // remove old observers
        searchResult.observe(getViewLifecycleOwner(), mails -> {
            if (mails != null && !mails.isEmpty()) {
                adapter.setMailList(mails);
                recyclerView.setVisibility(View.VISIBLE);
                noResultsText.setVisibility(View.GONE);
            } else {
                adapter.setMailList(Collections.emptyList());
                recyclerView.setVisibility(View.GONE);
                noResultsText.setText("No matches for \"" + query + "\"");
                noResultsText.setVisibility(View.VISIBLE);
            }
        });

        adapter.setOnItemClickListener(mail -> {
            Intent intent = new Intent(getContext(), MailViewActivity.class);
            intent.putExtra("mailId", mail.getId());
            intent.putExtra("subject", mail.getSubject());

            User sender = db.userDao().getUserById(userId);
            String senderEmail = sender != null ? sender.getMailAddress() : "Unknown";
            intent.putExtra("sender", senderEmail);

            intent.putExtra("body", mail.getContent());
            startActivity(intent);
        });

        return view;
    }
}



