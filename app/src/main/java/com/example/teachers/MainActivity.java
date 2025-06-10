package com.example.teachers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout btnClassList;
    Button settings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GridLayout dashboardGrid = findViewById(R.id.dashboardGrid);
        if (dashboardGrid != null && dashboardGrid.getChildCount() > 0) {
            dashboardGrid.getChildAt(0).setOnHoverListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start();
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                        break;
                }
                return false;
            });
        }




        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                }else if (itemId == R.id.nav_marks) {
                    startActivity(new Intent(this, EnterMarksActivity.class));
                    return false;
                }
                return false;
            });
        }
        ImageView settings = findViewById(R.id.settings_icon);
        settings.setOnClickListener(v -> {
            settings bottomSheet = new settings();
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });


    }
    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, settings.class);
        startActivity(intent);
    }

    public void openClassList(View view) {
        Intent intent = new Intent(this, ClassListActivity.class);
        startActivity(intent);
    }

    public void marks(View view) {
        Intent intent = new Intent(this, EnterMarksActivity.class);
        startActivity(intent);
    }

    public void openAttendance(View view) {
        Intent intent = new Intent(this, AttendanceActivity.class);
        startActivity(intent);
    }

    public void openAssignments(View view) {
        Intent intent = new Intent(MainActivity.this, AssignmentListActivity.class);
        startActivity(intent);
    }

    public void openTimeTable(MenuItem item) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }




}
