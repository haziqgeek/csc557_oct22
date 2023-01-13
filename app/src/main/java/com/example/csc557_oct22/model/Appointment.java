package com.example.csc557_oct22.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private User student;
    private int student_id;
    private User lecturer;
    private int lecturer_id;
    private String reason;
    private String status;
    private String date;
    private String time;

    public Appointment() {
    }

    public Appointment(int id, int student_id, int lecturer_id, String reason, String status, String date, String time) {
        this.id = id;
        this.student = student;
        this.student_id = student_id;
        this.lecturer = lecturer;
        this.lecturer_id = lecturer_id;
        this.reason = reason;
        this.status = status;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public User getLecturer() {
        return lecturer;
    }

    public void setLecturer(User lecturer) {
        this.lecturer = lecturer;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLecturer_id() {
        return lecturer_id;
    }

    public void setLecturer_id(int lecturer_id) {
        this.lecturer_id = lecturer_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", student_id=" + student_id +
                ", lecturer=" + lecturer +
                ", lecturer_id=" + lecturer_id +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
