package com.example.teachers;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private final List<Assignment> assignments;

    public AssignmentAdapter(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        Log.d("DEBUG_ASSIGNMENT", "Title: " + assignment.title + ", Class Name: " + assignment.class_name + ", Due: " + assignment.due_date);

        holder.title.setText(assignment.title);
        holder.className.setText("Class: " + assignment.class_name);
        holder.dueDate.setText("Due: " + assignment.due_date);

        holder.submissionContainer.setVisibility(View.GONE); // start collapsed

        holder.itemView.setOnClickListener(v -> {
            if (holder.submissionContainer.getVisibility() == View.GONE) {
                holder.submissionContainer.setVisibility(View.VISIBLE);
                fetchSubmissionData(assignment.title, holder); // أرسل title بدلاً من id
            } else {
                holder.submissionContainer.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    private void fetchSubmissionData(String assignmentTitle, ViewHolder holder) {
        String url = "http://10.0.2.2/get_submissions.php?assignment_title=" + Uri.encode(assignmentTitle);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray submitted = response.getJSONArray("submitted");
                        JSONArray notSubmitted = response.getJSONArray("not_submitted");

                        List<String> submittedNames = new ArrayList<>();
                        List<String> notSubmittedNames = new ArrayList<>();

                        for (int i = 0; i < submitted.length(); i++) {
                            submittedNames.add(submitted.getJSONObject(i).getString("name"));
                        }

                        for (int i = 0; i < notSubmitted.length(); i++) {
                            notSubmittedNames.add(notSubmitted.getJSONObject(i).getString("name"));
                        }

                        holder.submittedNames.setText(TextUtils.join("\n", submittedNames));
                        holder.notSubmittedNames.setText(TextUtils.join("\n", notSubmittedNames));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("SUBMISSION_ERROR", "Error: " + error.toString())
        );

        Volley.newRequestQueue(holder.itemView.getContext()).add(request);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, className, dueDate;
        LinearLayout submissionContainer;
        TextView submittedLabel, submittedNames, notSubmittedLabel, notSubmittedNames;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            className = itemView.findViewById(R.id.descriptionText);
            dueDate = itemView.findViewById(R.id.dueDateText);
            submissionContainer = itemView.findViewById(R.id.submissionContainer);
            submittedLabel = itemView.findViewById(R.id.submittedLabel);
            submittedNames = itemView.findViewById(R.id.submittedNames);
            notSubmittedLabel = itemView.findViewById(R.id.notSubmittedLabel);
            notSubmittedNames = itemView.findViewById(R.id.notSubmittedNames);
        }
    }
}
