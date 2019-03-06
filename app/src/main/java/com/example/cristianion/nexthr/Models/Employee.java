package com.example.cristianion.nexthr.Models;

import com.example.cristianion.nexthr.Utils.AESUtils;

public class Employee {

    public String id;
    public String lastName;
    public String firstName;
    public String birthday;
    public String password;
    public String phone;
    public String email;

    public Employee(){}
    public Employee(String id, String lastName, String firstName, String birthday,String password,String email,String phone){
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.phone = phone;
        try {
            this.password = AESUtils.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.email = email;
    }
}
