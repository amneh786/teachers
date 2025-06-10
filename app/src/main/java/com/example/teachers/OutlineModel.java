package com.example.teachers;

public class OutlineModel {
    private String title;
    private String description;
    private String className;
    private String subjectName;

    public OutlineModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public OutlineModel(String title, String description, String className, String subjectName) {
        this.title = title;
        this.description = description;
        this.className = className;
        this.subjectName = subjectName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getClassName() {
        return className;
    }

    public String getSubjectName() {
        return subjectName;
    }


}
