package com.example.cristianion.nexthr.Models;

public class Location {
    public String id;
    public String street;
    public String postalCode;
    public String city;
    public String county;
    public String country;


    public Location(){}
    public Location(String id,String street,String postalCode,String city,String county,String country){
        this.id = id;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.county = county;
    }
}
