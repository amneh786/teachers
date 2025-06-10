package com.example.teachers;

public class StudentMarkModel {
    private int studentId;
    private String name;
    private double enteredMark;

    public StudentMarkModel(int studentId, String name, double enteredMark) {
        this.studentId = studentId;
        this.name = name;
        this.enteredMark = enteredMark;
    }

    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public double getEnteredMark() { return enteredMark; }
    public void setEnteredMark(double mark) { this.enteredMark = mark; }
}
