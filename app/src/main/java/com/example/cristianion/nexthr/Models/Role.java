package com.example.cristianion.nexthr.Models;

public class Role {
    public String id;
    public String name;
    public String minSalary;
    public String maxSalary;

    public Role(){}
    public Role(String id,String name,String minSalary,String maxSalary){
        this.id = id;
        this.name = name;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }
}
