package com.example.teachers;

public class Student {
    private int id;
    private String name;
    private int classId;
    private boolean present;

    public Student(int id, String name, int classId) {
        this.id = id;
        this.name = name;
        this.classId = classId;
        this.present = false;
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getClassId() { return classId; }
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
}
