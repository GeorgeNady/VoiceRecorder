package com.example.voicerecorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicerecorder.R;
import com.example.voicerecorder.utile.CalculateTime;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioListViewHolder> {

    private File[] allFiles;
    private CalculateTime timeAgo;
    private onItemListClick onItemListClick;
    private LinearLayout container;

    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout2, parent, false);
        timeAgo = new CalculateTime();
        return new AudioListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioListViewHolder holder, int position) {
        holder.tvItemTitle.setText(allFiles[position].getName());
        holder.tvItemDate.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public interface onItemListClick {
        void onClickListener(File file, int position);
    }

    public class AudioListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageButton ibItemImage;
        private TextView tvItemTitle;
        private TextView tvItemDate;

        public AudioListViewHolder(@NonNull View itemView) {
            super(itemView);

            ibItemImage = itemView.findViewById(R.id.ibItemImage);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
            container = itemView.findViewById(R.id.container);

            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());
        }
    }
}
