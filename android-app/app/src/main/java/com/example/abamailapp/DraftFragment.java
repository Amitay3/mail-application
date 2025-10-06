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
import com.example.abamailapp.repositories.UserRepository;
import com.example.abamailapp.viewmodels.MailViewModel;

public class DraftFragment extends Fragment {

    private MailViewModel mailViewModel;
    private MailListAdapter adapter;
    private RecyclerView recyclerView;
    private AppDB db;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drafts, container, false);
        // Title
        TextView folderTitle = view.findViewById(R.id.folderTitle);
        folderTitle.setText("Drafts");

        recyclerView = view.findViewById(R.id.recyclerViewDrafts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MailListAdapter();
        recyclerView.setAdapter(adapter);

        db = DatabaseClient.getInstance(getContext());
        int userId = SessionManager.getLoggedInUserId();
        userRepository = new UserRepository(getContext());
        mailViewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);

        User user = db.userDao().getUserById(userId);

        if (user == null) {
            // User not in Room, fetch from backend
            userRepository.fetchUserById(userId, fetchedUser -> {
                if (fetchedUser != null) {
                    new Thread(() -> {
                        db.userDao().insert(fetchedUser); // save to Room
                        requireActivity().runOnUiThread(() -> {
                            mailViewModel.getDrafts(fetchedUser.getMailAddress())
                                    .observe(getViewLifecycleOwner(), mails -> adapter.setMailList(mails));
                        });
                    }).start();
                }
            });
        } else {
            // User exists in Room
            mailViewModel.getDrafts(user.getMailAddress()).removeObservers(getViewLifecycleOwner());
            mailViewModel.getDrafts(user.getMailAddress())
                    .observe(getViewLifecycleOwner(), mails -> adapter.setMailList(mails));
        }
        // Handle item clicks
        adapter.setOnItemClickListener(mail -> {
            Intent intent = new Intent(getContext(), ComposeActivity.class);
            intent.putExtra("id", mail.getId());
            intent.putExtra("backendId", mail.getBackendId());
            intent.putExtra("isDraft", mail.isDraft());
            intent.putExtra("to", mail.getRecipientEmail());
            intent.putExtra("subject", mail.getSubject());
            intent.putExtra("body", mail.getContent());
            startActivity(intent);
        });

        return view;
    }
}
