package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ClassItem> classList = new ArrayList<>();
    ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView = findViewById(R.id.class_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(this, classList);
        recyclerView.setAdapter(adapter);
        SharedPreferences prefs1 = getSharedPreferences("session", MODE_PRIVATE);
        boolean isHomeroom = prefs1.getBoolean("isHomeroom", false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    if(isHomeroom) {
                        startActivity(new Intent(this, MainActivity.class));
                    }else{
                        startActivity(new Intent(this, NormalTeacher.class));
                    }
                    return true;
                }
                return false;
            });
        }

        loadClasses();
    }

    private void loadClasses() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        if (userId == null) return;

        String url = "http://10.0.2.2/get_classes.php?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        com.android.volley.toolbox.StringRequest request = new com.android.volley.toolbox.StringRequest(
                Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject res = new JSONObject(response);
                        if (res.getString("status").equals("success")) {
                            JSONArray dataArray = res.getJSONArray("data");
                            classList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                String className = obj.getString("class_name");
                                String subjectName = obj.getString("subject_name");
                                classList.add(new ClassItem(className, subjectName));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, res.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Volley error", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }



    public void openTimeTable(MenuItem item) {
        startActivity(new Intent(this, TimetableActivity.class));
    }
}
