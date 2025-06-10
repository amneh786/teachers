package com.example.teachers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentsMarksAdapter extends RecyclerView.Adapter<StudentsMarksAdapter.MarkViewHolder> {

    private List<StudentMarkModel> studentList;

    public StudentsMarksAdapter(List<StudentMarkModel> studentList) {
        this.studentList = studentList;
    }

    public StudentsMarksAdapter() {
    }

    @NonNull
    @Override
    public MarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_mark, parent, false);
        return new MarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkViewHolder holder, int position) {
        StudentMarkModel student = studentList.get(position);
        holder.name.setText(student.getName());
        holder.markInput.setText(String.valueOf(student.getEnteredMark()));


        holder.markInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    student.setEnteredMark(Double.parseDouble(s.toString()));
                } catch (NumberFormatException e) {
                    student.setEnteredMark(0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class MarkViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        EditText markInput;

        MarkViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.studentNameText);
            markInput = itemView.findViewById(R.id.studentMarkInput);
        }
    }
}

