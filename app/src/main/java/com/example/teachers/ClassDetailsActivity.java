package com.example.teachers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClassDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OutlineAdapter adapter;
    private List<OutlineModel> outlineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        String subjectName = getIntent().getStringExtra("subject_name");

        if (userId != null && subjectName != null) {
            fetchOutlines(userId, subjectName);
        } else {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchOutlines(String userId, String subjectName) {
        String url = "http://10.0.2.2/get_outline.php?user_id=" + userId + "&subject_name=" + subjectName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            outlineList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                String title = obj.getString("title");
                                String description = obj.getString("description");
                                String className = obj.getString("class_name");
                                String subject = obj.getString("subject_name");
                                outlineList.add(new OutlineModel(title, description, className, subject));
                            }
                            adapter = new OutlineAdapter(this, outlineList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "No outlines found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }

}
