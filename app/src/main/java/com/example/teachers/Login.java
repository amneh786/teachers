package com.example.teachers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnLogin;
    CheckBox checkboxRemember;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.etEmail);
        txtPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        checkboxRemember = findViewById(R.id.checkboxRemember);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        boolean remember = sharedPreferences.getBoolean("remember", false);
        if (remember) {
            txtEmail.setText(sharedPreferences.getString("email", ""));
            txtPassword.setText(sharedPreferences.getString("password", ""));
            checkboxRemember.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkboxRemember.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("remember", true);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();
            } else {
                sharedPreferences.edit().clear().apply();
            }

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://10.0.2.2/login.php?email=" + email + "&password=" + password;

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JSONObject jsonBody = new JSONObject(params);

        Log.d("LOGIN_REQUEST", "Sending: " + jsonBody.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        Log.d("LOGIN_RESPONSE", response.toString());

                        if (response.getString("status").equals("success")) {
                            String userId = response.getString("user_id");
                            String role = response.getString("role");
                            String name = response.getString("name");
                            boolean isHomeroom = response.getBoolean("homeroom");

                            SharedPreferences sessionPrefs = getSharedPreferences("session", MODE_PRIVATE);
                            SharedPreferences.Editor sessionEditor = sessionPrefs.edit();
                            sessionEditor.putString("id", userId);
                            sessionEditor.putString("role", role);
                            sessionEditor.putString("name", name);
                            sessionEditor.putString("email", email);
                            sessionEditor.putBoolean("isHomeeroom", isHomeroom);
                            sessionEditor.apply();

                            Intent intent;
                            if (role.equals("teacher") && isHomeroom) {
                                intent = new Intent(this, MainActivity.class);
                            } else if (role.equals("teacher")) {
                                intent = new Intent(this, NormalTeacher.class);
                            } else {
                                Toast.makeText(this, "Unsupported role", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }
}
