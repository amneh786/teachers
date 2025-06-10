package com.example.teachers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private Context context;
    private List<ClassItem> classList;

    public ClassAdapter(Context context, List<ClassItem> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_card, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem classItem = classList.get(position);
        holder.className.setText(classItem.getClassName() + " - " + classItem.getSubjectName());

        if (classItem.isExpanded()) {
            holder.outlineRecyclerView.setVisibility(View.VISIBLE);
            loadOutlines(classItem.getSubjectName(), holder.outlineRecyclerView, classItem);
        } else {
            holder.outlineRecyclerView.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            classItem.setExpanded(!classItem.isExpanded());
            notifyItemChanged(position);
        });
    }

    private void loadOutlines(String subjectName, RecyclerView recyclerView, ClassItem classItem) {
        SharedPreferences prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        if (userId == null) return;

        String url = "http://10.0.2.2/get_outline.php?user_id=" + userId + "&subject_name=" + subjectName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (!response.getString("status").equals("success")) return;

                        JSONArray array = response.getJSONArray("data");
                        List<OutlineModel> outlines = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String title = obj.getString("title");
                            String description = obj.getString("description");
                            String className = obj.getString("class_name");
                            String subjName = obj.getString("subject_name");
                            outlines.add(new OutlineModel(title, description, className, subjName));
                        }

                        classItem.setOutlines(outlines);
                        OutlineAdapter outlineAdapter = new OutlineAdapter(context, outlines);
                        recyclerView.setAdapter(outlineAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        RecyclerView outlineRecyclerView;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            outlineRecyclerView = itemView.findViewById(R.id.outlineRecyclerView);
            outlineRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
