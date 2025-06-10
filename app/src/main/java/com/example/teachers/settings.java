package com.example.teachers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class settings extends BottomSheetDialogFragment {

    private TextView userEmail, userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userEmail = view.findViewById(R.id.userEmail);
        userName = view.findViewById(R.id.userName);

       SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "No Email");
        String name = sharedPreferences.getString("name", "User");

        userEmail.setText(email);
        userName.setText(name);
    }
}
