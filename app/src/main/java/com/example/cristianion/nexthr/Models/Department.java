package com.example.cristianion.nexthr.Models;

public class Department {
    public String id;
    public String name;
    public String managerId;
    public String locationId;

    public Department(){}
    public Department(String id,String name,String managerId,String locationId){
        this.id = id;
        this.name = name;
        this.managerId = managerId;
        this.locationId = locationId;
    }
}
