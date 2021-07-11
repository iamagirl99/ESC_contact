package com.example.esc_contact;

public class Contact {
    String phoneNumber;
    String name;
    Long id;

    public Contact(){}

    public Contact(String phoneNumber, String name, long id){
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

}
