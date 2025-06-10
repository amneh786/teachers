package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button submitButton;
    List<Student> students = new ArrayList<>();
    AttendanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);

        TextView currentDateTextView = findViewById(R.id.current_date);
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        currentDateTextView.setText(currentDate);

        recyclerView = findViewById(R.id.attendance_recycler);
        submitButton = findViewById(R.id.submit_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(students);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_Timetable) {
                    return false;
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    return false;
                } else if (itemId == R.id.nav_marks) {
                    startActivity(new Intent(this, EnterMarksActivity.class));
                    return false;
                }
                return false;
            });
        }

        fetchStudents();
        submitButton.setOnClickListener(v -> submitAttendance());
    }

    private void fetchStudents() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        if (userId == null) {
            Toast.makeText(this, "No teacher session found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/get_students.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject resObj = new JSONObject(response);
                        if (!resObj.getString("status").equals("success")) {
                            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray array = resObj.getJSONArray("data");
                        students.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            students.add(new Student(id, name));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing students", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void submitAttendance() {
        String url = "http://10.0.2.2/submit_attendance.php";

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        if (userId == null) {
            Toast.makeText(this, "No teacher session found", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject requestBody = new JSONObject();
        JSONArray attendanceArray = new JSONArray();

        for (Student student : students) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", student.getName());
                obj.put("class_id", student.getClassId());
                obj.put("status", student.isPresent() ? "Present" : "Absent");
                obj.put("user_id", userId);
                attendanceArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            requestBody.put("attendance", attendanceArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.d("ATTENDANCE_RESPONSE", response.toString()); 
                    Toast.makeText(this, "Attendance Submitted", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Submission Failed", Toast.LENGTH_SHORT).show();
                }
        );


        Volley.newRequestQueue(this).add(request);
    }



    public void openTimeTable(MenuItem item) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
}
