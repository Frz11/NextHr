package com.example.cristianion.nexthr.Models;

public class Attendance {
    public String id;
    public String arrivalTime;
    public String leaveTime;
    public String employeeId;
    public String day;

    public Attendance() {}

    public Attendance(String id,String arrivalTime,String leaveTime,String employeeId,String day){
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.leaveTime = leaveTime;
        this.employeeId = employeeId;
        this.day = day;

    }
}
