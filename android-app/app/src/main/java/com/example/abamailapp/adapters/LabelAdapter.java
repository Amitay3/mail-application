package com.example.abamailapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abamailapp.R;
import com.example.abamailapp.entities.Label;

import java.util.ArrayList;
import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {
    private List<Label> labels = new ArrayList<>();
    private OnLabelActionListener listener;

    public interface OnLabelActionListener {
        void onEdit(Label label);
        void onDelete(Label label);
    }


    public LabelAdapter(OnLabelActionListener listener) {
        this.listener = listener;
    }
    public void setLabels(List<Label> labels) {
        this.labels = labels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_item, parent, false);
        return new LabelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        Label label = labels.get(position);
        holder.labelName.setText(label.getName());

        holder.labelMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.labelMenu);
            popup.inflate(R.menu.label_item_menu);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    listener.onEdit(label);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    listener.onDelete(label);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    static class LabelViewHolder extends RecyclerView.ViewHolder {
        TextView labelName;
        ImageView labelIcon;
        ImageView labelMenu;

        LabelViewHolder(View itemView) {
            super(itemView);
            labelName = itemView.findViewById(R.id.label_name);
            labelIcon = itemView.findViewById(R.id.label_icon);
            labelMenu = itemView.findViewById(R.id.label_menu);
        }
    }
}

