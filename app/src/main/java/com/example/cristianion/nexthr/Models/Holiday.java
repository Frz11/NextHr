package com.example.cristianion.nexthr.Models;

import java.util.ArrayList;
import java.util.List;

public class Holiday {
    public String id;
    public String employeeId;
    public ArrayList<String> dates = new ArrayList<>();
    public String status;
    public String managerId;


    public Holiday(){}

    public Holiday(String id, String employeeId, ArrayList<String> dates,String status,String managerId){
        this.id = id;
        this.employeeId = employeeId;
        this.dates = dates;
        this.status = status;
        this.managerId = managerId;
    }
}
