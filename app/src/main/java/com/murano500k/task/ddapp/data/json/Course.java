
package com.murano500k.task.ddapp.data.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mark")
    @Expose
    private int mark;

    public Course(String name, int mark) {
        this.name = name;
        this.mark = mark;
    }

    public Course() {
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", mark=" + mark +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

}
