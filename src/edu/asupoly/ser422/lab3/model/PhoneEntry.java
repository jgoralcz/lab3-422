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


    public void changeName(String newfname, String newlname) {
    	firstName = newfname;
    	// This is here to introduce artifical latency for testing purposes
    	try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	lastName  = newlname;
    }

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

    public String toString() { return firstName + "\n" + lastName + "\n" + phoneNumber + "\n" + phoneBookID; }
}



