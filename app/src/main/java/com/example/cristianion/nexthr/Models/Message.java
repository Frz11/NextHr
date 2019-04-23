package com.example.cristianion.nexthr.Models;

public class Message {

    public String id;
    public String from;
    public String to;
    public String message;
    public String sentAt;


    public Message(){}
    public Message(String id, String from, String to, String message, String sentAt){
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.sentAt = sentAt;
    }

}
