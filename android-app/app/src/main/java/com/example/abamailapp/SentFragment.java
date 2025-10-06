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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abamailapp.adapters.MailListAdapter;
import com.example.abamailapp.entities.User;
import com.example.abamailapp.viewmodels.MailViewModel;

public class SentFragment extends Fragment {

    private MailViewModel mailViewModel;
    private MailListAdapter adapter;
    private RecyclerView recyclerView;
    private AppDB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sent, container, false);
        // Title
        TextView folderTitle = view.findViewById(R.id.folderTitle);
        folderTitle.setText("Sent");

        recyclerView = view.findViewById(R.id.recyclerViewSent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MailListAdapter();
        // For displaying self sent mails
        adapter.setCurrentFolder("sent");
        recyclerView.setAdapter(adapter);

        db = DatabaseClient.getInstance(getContext());
        int userId = SessionManager.getLoggedInUserId();

        String userEmail = db.userDao().getUserById(userId).getMailAddress();

        mailViewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);

        // Observe sent mails
        mailViewModel.getSent(userEmail).observe(getViewLifecycleOwner(), mails -> {
            adapter.setMailList(mails);
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
