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
import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.User;
import com.example.abamailapp.repositories.UserRepository;
import com.example.abamailapp.viewmodels.MailViewModel;

public class LabelMailsFragment extends Fragment {

    private static final String ARG_LABEL_BACKEND_ID = "label_backend_id";
    private String labelBackendId;

    private MailViewModel mailViewModel;
    private MailListAdapter adapter;
    private RecyclerView recyclerView;
    private AppDB db;

    public static LabelMailsFragment newInstance(String labelBackendId) {
        LabelMailsFragment fragment = new LabelMailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL_BACKEND_ID, labelBackendId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            labelBackendId = getArguments().getString(ARG_LABEL_BACKEND_ID);
        }
        db = DatabaseClient.getInstance(requireContext());
        mailViewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_label_mails, container, false);

        TextView folderTitle = view.findViewById(R.id.folderTitle);
        Label label = db.labelDao().getLabelById(labelBackendId);
        folderTitle.setText(label.getName());

        recyclerView = view.findViewById(R.id.recyclerViewLabelMails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MailListAdapter();
        recyclerView.setAdapter(adapter);

        db = DatabaseClient.getInstance(getContext());
        int userId = SessionManager.getLoggedInUserId();
        UserRepository userRepository = new UserRepository(getContext());
        mailViewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);


        User user = db.userDao().getUserById(userId);

        if (user == null) {
            // User not in Room, fetch from backend
            userRepository.fetchUserById(userId, fetchedUser -> {
                if (fetchedUser != null) {
                    new Thread(() -> {
                        db.userDao().insert(fetchedUser);
                        requireActivity().runOnUiThread(() -> {
                            mailViewModel.getInbox(fetchedUser.getMailAddress())
                                    .observe(getViewLifecycleOwner(), mails -> adapter.setMailList(mails));
                        });
                    }).start();
                }
            });
        } else {
            // User exists in Room
            mailViewModel.fetchMailsForLabel(labelBackendId).removeObservers(getViewLifecycleOwner());
            mailViewModel.fetchMailsForLabel(labelBackendId)
                    .observe(getViewLifecycleOwner(), mails -> adapter.setMailList(mails));

        }

        adapter.setOnItemClickListener(mail -> {
            Intent intent = new Intent(getContext(), MailViewActivity.class);
            intent.putExtra("mailId", mail.getId());
            intent.putExtra("subject", mail.getSubject());
            intent.putExtra("sender", mail.getSenderEmail());
            intent.putExtra("body", mail.getContent());
            startActivity(intent);
        });
        return view;
    }
}