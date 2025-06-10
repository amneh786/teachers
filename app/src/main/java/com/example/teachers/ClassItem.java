package com.example.teachers;


import java.util.ArrayList;
import java.util.List;


public class ClassItem {
    private int id;
    private String name;
    private List<OutlineModel> outlines;
    private boolean expanded;

    public ClassItem(int id, String name) {
        this.id = id;
        this.name = name;
        this.outlines = new ArrayList<>();
        this.expanded = false;
    }

    String className;
    String subjectName;

    public ClassItem(String className, String subjectName) {
        this.className = className;
        this.subjectName = subjectName;
    }

    public String getClassName() {
        return className;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<OutlineModel> getOutlines() { return outlines; }
    public void setOutlines(List<OutlineModel> outlines) { this.outlines = outlines; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}
