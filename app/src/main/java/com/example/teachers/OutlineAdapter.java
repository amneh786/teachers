package com.example.teachers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

    public class OutlineAdapter extends RecyclerView.Adapter<OutlineAdapter.ViewHolder> {
        private final Context context;
        private final List<OutlineModel> outlines;

        public OutlineAdapter(Context context, List<OutlineModel> outlines) {
            this.context = context;
            this.outlines = outlines;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.outline_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OutlineModel item = outlines.get(position);
            holder.title.setText(item.getTitle());
            holder.description.setText(item.getDescription());
            holder.className.setText("Class: " + item.getClassName());
            holder.subjectName.setText("Subject: " + item.getSubjectName());
        }

        @Override
        public int getItemCount() {
            return outlines != null ? outlines.size() : 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, className, subjectName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.outlineTitle);
                description = itemView.findViewById(R.id.outlineDescription);
                className = itemView.findViewById(R.id.outlineClassName);
                subjectName = itemView.findViewById(R.id.outlineSubjectName);
            }
        }
}
