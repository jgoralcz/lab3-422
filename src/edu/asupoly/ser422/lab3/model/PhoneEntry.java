package edu.asupoly.ser422.lab3.model;

public class PhoneEntry {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneBookID;

    public PhoneEntry(String phoneNumber, String phoneBookID, String firstName, String lastName) {
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.phoneNumber = phoneNumber;
        this.phoneBookID = phoneBookID;
    }

    public PhoneEntry() {}

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneBookID() {
        return phoneBookID;
    }



    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneBookID(String phoneBookID) {
        this.phoneBookID = phoneBookID;
    }

    public String toString() { return firstName + "\n" + lastName + "\n" + phoneNumber + "\n" + phoneBookID; }
}



