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
import com.example.abamailapp.viewmodels.MailViewModel;

public class SpamFragment extends Fragment {

    private MailViewModel mailViewModel;
    private MailListAdapter adapter;
    private RecyclerView recyclerView;
    private AppDB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_spam, container, false);
        // Title
        TextView folderTitle = view.findViewById(R.id.folderTitle);
        folderTitle.setText("Spam");

        recyclerView = view.findViewById(R.id.recyclerViewSpam);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MailListAdapter();
        recyclerView.setAdapter(adapter);

        db = DatabaseClient.getInstance(getContext());
        int userId = SessionManager.getLoggedInUserId();

        String userEmail = db.userDao().getUserById(userId).getMailAddress();

        mailViewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);

        // Observe spam mails
        mailViewModel.getSpam(userEmail).observe(getViewLifecycleOwner(), mails -> {
            adapter.setMailList(mails);
        });

        adapter.setOnItemClickListener(mail -> {
            Intent intent = new Intent(getContext(), MailViewActivity.class);
            intent.putExtra("mailId", mail.getId());
            intent.putExtra("subject", mail.getSubject());

            String senderEmail = mail.getSenderEmail() != null ? mail.getSenderEmail() : "Unknown";
            intent.putExtra("sender", senderEmail);

            intent.putExtra("body", mail.getContent());
            startActivity(intent);
        });

        return view;
    }
}
