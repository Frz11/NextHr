package com.example.cristianion.nexthr.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Company {

    private DatabaseReference mDatabase;


    public String name;
    public String id;

    public Company() {}
    public Company(String id){

        this.id = id;
    }

    public Company(String id,String name){
        this.name = name;
        this.id = id;
    }
    public void WriteToDatabase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("companies").child(this.id).setValue(this);

    }

}
