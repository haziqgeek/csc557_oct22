package com.example.csc557_oct22.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private String reason;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private int stuId;
    private int lectId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public int getLectId() {
        return lectId;
    }

    public void setLectId(int lectId) {
        this.lectId = lectId;
    }
}
