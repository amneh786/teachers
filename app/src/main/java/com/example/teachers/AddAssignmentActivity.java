package com.example.teachers;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddAssignmentActivity extends AppCompatActivity {

    EditText titleInput, descriptionInput, dueDateInput, classIdInput, subjectIdInput;
    Button submitAssignmentBtn;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        classIdInput = findViewById(R.id.classIdInput);
        subjectIdInput = findViewById(R.id.subjectIdInput);
        submitAssignmentBtn = findViewById(R.id.submitAssignmentBtn);
        backBtn = findViewById(R.id.back);

        dueDateInput.setOnClickListener(v -> showDatePicker());
        backBtn.setOnClickListener(v -> finish());

        submitAssignmentBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
            String userId = prefs.getString("id", null); // user_id مش teacher_id

            if (userId == null) {
                Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String dueDate = dueDateInput.getText().toString().trim();
            String className = classIdInput.getText().toString().trim();
            String subjectName = subjectIdInput.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()
                    || className.isEmpty() || subjectName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2/add_assignment.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(this, "Assignment saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Error saving assignment", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", title);
                    params.put("description", description);
                    params.put("due_date", dueDate);
                    params.put("class_name", className);
                    params.put("subject_name", subjectName);
                    params.put("user_id", userId);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            dueDateInput.setText(date);
        }, year, month, day).show();
    }
}
