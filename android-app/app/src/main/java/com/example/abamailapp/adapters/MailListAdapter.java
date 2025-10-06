package com.example.abamailapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abamailapp.DatabaseClient;
import com.example.abamailapp.R;
import com.example.abamailapp.SessionManager;
import com.example.abamailapp.entities.Mail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MailListAdapter extends RecyclerView.Adapter<MailListAdapter.MailViewHolder> {

    private List<Mail> mailList = new ArrayList<>();
    private OnItemClickListener listener;

    // To handle special display cases based on folder context
    private String currentFolder = "";
    public void setCurrentFolder(String folder) {
        this.currentFolder = folder;
    }

    public interface OnItemClickListener {
        void onItemClick(Mail mail);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMailList(List<Mail> mails) {
        this.mailList.clear();
        if (mails != null) {
            this.mailList.addAll(mails);
        }
        notifyDataSetChanged();
    }


    public List<Mail> getMailList() {
        return mailList;
    }

    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mail_item, parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        Mail mail = mailList.get(position);
        holder.bind(mail);
    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }
    // Format the display address (to/from/me)
    public static String formatDisplayAddress(Mail mail, String myEmail) {
        String sender = mail.getSenderEmail();
        String recipient = mail.getRecipientEmail();

        if (sender.equals(myEmail) && recipient.equals(myEmail)) {
            // self mail
            return "me";
        } else if (sender.equals(myEmail)) {
            // out mail
            return "To: " + recipient;
        } else {
            // in mail
            return sender;
        }
    }
    class MailViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFrom, tvSubject, tvPreview, tvDate;

        MailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFrom = itemView.findViewById(R.id.textSender);
            tvSubject = itemView.findViewById(R.id.textSubject);
            tvPreview = itemView.findViewById(R.id.textSnippet);
            tvDate = itemView.findViewById(R.id.textTime);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(mailList.get(getAdapterPosition()));
                }
            });
        }

        void bind(Mail mail) {
            // Fetch user email
            String myEmail = DatabaseClient.getInstance(itemView.getContext())
                    .userDao()
                    .getUserById(SessionManager.getLoggedInUserId())
                    .getMailAddress();

            // Basic display
            String display = formatDisplayAddress(mail, myEmail);

            // Edge case: self mail in Sent folder
            if (mail.getSenderEmail().equals(myEmail) &&
                    mail.getRecipientEmail().equals(myEmail) &&
                    currentFolder.equals("sent")) {
                display = "To: me";
            }

            tvFrom.setText(display);
            tvSubject.setText(mail.getSubject());
            tvPreview.setText(mail.getContent().length() > 50 ?
                    mail.getContent().substring(0, 50) + "..." : mail.getContent());
            String time = mail.getTime();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            try {
                Date date = parser.parse(time);
                tvDate.setText(formatter.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                tvDate.setText(time);
            }
        }
    }
}
