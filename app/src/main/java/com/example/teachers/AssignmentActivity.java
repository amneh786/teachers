package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignmentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AssignmentAdapter adapter;
    List<Assignment> assignmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignments);

        recyclerView = findViewById(R.id.assignmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);

        fetchAssignments();

        FloatingActionButton addBtn = findViewById(R.id.addAssignmentButton);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AssignmentActivity.this, AddAssignmentActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAssignments() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("id", null);

        if (userId == null) {
            Toast.makeText(this, "No user session found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/get_assignments.php?user_id=" + userId;
        Log.d("VOLLEY_DEBUG", "Request URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("VOLLEY_SUCCESS", "Response: " + response.toString());
                    parseAssignments(response);
                },
                error -> {
                    String message;
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseData = new String(error.networkResponse.data);
                        message = "Status: " + statusCode + "\nResponse: " + responseData;
                    } else {
                        message = error.toString();
                    }
                    Log.e("VOLLEY_ERROR", message);
                    Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void parseAssignments(JSONArray response) {
        assignmentList.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Assignment assignment = new Assignment();

                assignment.id = obj.getInt("id");
                assignment.title = obj.getString("title");
                assignment.description = obj.getString("description");
                assignment.due_date = obj.getString("due_date");
                assignment.class_id = obj.getString("class_name");

                assignmentList.add(assignment);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("VOLLEY_PARSE", "Error parsing JSON: " + e.getMessage());
        }
    }
}
