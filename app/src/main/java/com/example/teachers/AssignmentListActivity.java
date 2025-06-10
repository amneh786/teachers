package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignmentListActivity extends AppCompatActivity {

    RecyclerView assignmentsRecyclerView;
    AssignmentAdapter adapter;
    List<Assignment> assignmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignments);

        assignmentsRecyclerView = findViewById(R.id.assignmentsRecyclerView);
        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(assignmentList);
        assignmentsRecyclerView.setAdapter(adapter);

        fetchAssignments();

        SharedPreferences prefs1 = getSharedPreferences("session", MODE_PRIVATE);
        boolean isHomeroom = prefs1.getBoolean("isHomeroom", false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                if(isHomeroom) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(this, NormalTeacher.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
            return false;
        });

        FloatingActionButton addBtn = findViewById(R.id.addAssignmentButton);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAssignmentActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAssignments() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("id", null);

        Log.d("USER_ID", "user_id = " + userId);
        if (userId == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = "http://10.0.2.2/get_assignments.php?user_id=" + userId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    assignmentList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Assignment assignment = new Assignment();
                            assignment.title = obj.getString("assignment_title");
                            assignment.class_name = obj.getString("class_name");
                            assignment.due_date = obj.getString("due_date");
                            assignmentList.add(assignment);
                            Log.d("Asssss", "Data: " + assignmentList.get(i).title);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Volley error: " + error.toString(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }




    public void openTimeTable(MenuItem item) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
}
