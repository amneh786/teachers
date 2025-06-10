package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterMarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentsMarksAdapter adapter;
    private List<StudentMarkModel> studentList = new ArrayList<>();

    private Spinner examTypeSpinner;
    private Spinner markClassSpinner;
    private Button submitButton;

    private List<Integer> classIds = new ArrayList<>();
    private List<String> classNames = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_marks);

        recyclerView = findViewById(R.id.studentsRecyclerView);
        examTypeSpinner = findViewById(R.id.markTypeSpinner);
        markClassSpinner = findViewById(R.id.markClassSpinner);
        submitButton = findViewById(R.id.submitMarksButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentsMarksAdapter(studentList);
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Assignment", "Mid Exam", "Final Exam"));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examTypeSpinner.setAdapter(spinnerAdapter);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        userId = Integer.parseInt(prefs.getString("id", "1"));

        loadTeacherClasses();

        submitButton.setOnClickListener(v -> submitMarks());

        SharedPreferences prefs1 = getSharedPreferences("session", MODE_PRIVATE);
        boolean isHomeroom = prefs1.getBoolean("isHomeroom", false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_home) {
                    if(isHomeroom) {
                        startActivity(new Intent(this, MainActivity.class));
                    }else{
                        startActivity(new Intent(this, NormalTeacher.class));

                    }
                    return true;
                } else if (item.getItemId() == R.id.nav_Timetable) {
                    openTimeTable(item);
                    return true;
                }
                return false;
            });
        }

        markClassSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedClassName = classNames.get(position);
                String selectedType = examTypeSpinner.getSelectedItem().toString();
                loadStudentsFromDatabase(selectedClassName, selectedType);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                studentList.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadTeacherClasses() {
        String url = "http://10.0.2.2/get_taught_classes.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject resObj = new JSONObject(response);
                        if (!resObj.getString("status").equals("success")) return;

                        JSONArray array = resObj.getJSONArray("classes");
                        classNames.clear();
                        classIds.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            classNames.add(obj.getString("name"));
                            classIds.add(obj.getInt("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, classNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        markClassSpinner.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading classes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to load classes", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadStudentsFromDatabase(String className, String type) {
        studentList.clear();
        adapter.notifyDataSetChanged();

        String url = "http://10.0.2.2/get_student_for_class.php?user_id=" + userId + "&class_name=" + className + "&type=" + type ;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        if (!responseObject.getString("status").equals("success")) {
                            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray array = responseObject.getJSONArray("students");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("student_id");
                            String name = obj.getString("student_name");
                            String markStr = obj.getString("mark");
                            double mark = markStr.isEmpty() ? 0.0 : Double.parseDouble(markStr);
                            studentList.add(new StudentMarkModel(id, name, mark));

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


    private void submitMarks() {
        int selectedClassIndex = markClassSpinner.getSelectedItemPosition();
        if (selectedClassIndex < 0 || selectedClassIndex >= classIds.size()) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(selectedClassIndex);
        String type = examTypeSpinner.getSelectedItem().toString();

        for (StudentMarkModel student : studentList) {
            double mark = student.getEnteredMark();

            JSONObject data = new JSONObject();
            try {
                Log.d("submitMarks", "Submitting marks for user_id: " + userId);
                data.put("student_id", student.getStudentId());
                data.put("user_id", userId);
                data.put("class_id", classId);
                data.put("mark", mark);
                data.put("type", type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2/submit_mark.php",
                    data,
                    response -> {},
                    error -> {}
            );
            Log.d("submitMarks", "Data: " + data.toString());


            Volley.newRequestQueue(this).add(request);
        }

        Toast.makeText(this, "Marks submitted", Toast.LENGTH_SHORT).show();
    }

    public void openTimeTable(MenuItem item) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
}