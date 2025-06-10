package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;

public class TimetableActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private TableRow[] tableRows = new TableRow[5];
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);

        tableLayout = findViewById(R.id.timetable_table);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int userId = Integer.parseInt(prefs.getString("id", "1"));
        URL = "http://10.0.2.2/get_timetable.php?user_id=" + userId;

        buildTable();
        loadTimetableData();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        SharedPreferences prefs1 = getSharedPreferences("session", MODE_PRIVATE);
        boolean isHomeroom = prefs1.getBoolean("isHomeroom", false);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_Timetable) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    if(isHomeroom){
                    startActivity(new Intent(this, MainActivity.class));}
                    else{
                        startActivity(new Intent(this, NormalTeacher.class));
                    }
                    return false;
                } else if (itemId == R.id.nav_marks) {
                    startActivity(new Intent(this, EnterMarksActivity.class));
                    return false;
                }
                return false;
            });
        }
    }

    private void buildTable() {
        String[] times = { "8:00", "9:00", "10:00", "11:00", "12:00" };

        TableRow header = new TableRow(this);
        tableLayout.addView(header);

        for (int i = 0; i < times.length; i++) {
            TableRow row = new TableRow(this);
            row.addView(createCell(times[i]));

            for (int j = 0; j < 5; j++) {
                row.addView(createCell(""));
            }

            tableRows[i] = row;
            tableLayout.addView(row);
        }
    }

    private void loadTimetableData() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            String day = item.getString("day");
                            String time = item.getString("time_slot");
                            String subject = item.getString("subject_name");
                            String className = item.getString("class_name");

                            int rowIndex = getRowFromTime(time);
                            int colIndex = getColumnFromDay(day);

                            if (rowIndex != -1 && colIndex != -1) {
                                TableRow row = tableRows[rowIndex];
                                TextView cell = (TextView) row.getChildAt(colIndex + 1);
                                cell.setText(subject + "\n(" + className + ")");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        queue.add(request);
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setMaxLines(2);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getColor(android.R.color.black));
        tv.setTextSize(12);
        tv.setBackgroundResource(R.drawable.cell_border);
        tv.setPadding(16, 16, 16, 16);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                dpToPx(80),
                1f
        );
        tv.setLayoutParams(params);

        return tv;
    }

    private int dpToPx(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private TextView createHeaderCell(String text) {
        TextView tv = createCell(text);
        tv.setBackgroundColor(getColor(R.color.headerBackground));
        tv.setTextColor(getColor(android.R.color.white));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }

    private int getColumnFromDay(String day) {
        switch (day.toLowerCase()) {
            case "sunday": return 0;
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            default: return -1;
        }
    }

    private int getRowFromTime(String time) {
        switch (time) {
            case "8:00 - 9:00 AM":
            case "08:00 - 09:00":
                return 0;
            case "9:00 - 10:00 AM":
            case "09:00 - 10:00":
                return 1;
            case "10:00 - 11:00 AM":
            case "10:00 - 11:00":
                return 2;
            case "11:00 - 12:00 PM":
            case "11:00 - 12:00":
                return 3;
            case "12:00 - 1:00 PM":
            case "12:00 - 13:00":
                return 4;
            default:
                return -1;
        }
    }

    public void openTimeTable(MenuItem item) {}
}
