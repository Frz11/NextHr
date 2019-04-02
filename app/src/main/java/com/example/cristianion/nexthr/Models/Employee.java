package com.example.cristianion.nexthr.Models;

import com.example.cristianion.nexthr.Utils.AESUtils;

public class Employee {

    public String id;
    public String lastName;
    public String firstName;
    public String birthday;
    public String phone;
    public String email;
    public String companyId;
    public String salary;
    public String departmentId;

    public Employee(){}
    public Employee(String id, String lastName, String firstName, String birthday,String email,String phone,String companyId){
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.companyId = companyId;
        this.salary = "";
        this.departmentId = "";
    }
    public Employee(String id, String lastName, String firstName, String birthday,String email,String phone,String companyId,String salary,String departmentId){
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.companyId = companyId;
        this.salary = salary;
        this.departmentId = departmentId;
    }
}
