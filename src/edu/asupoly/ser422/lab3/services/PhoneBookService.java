package edu.asupoly.ser422.lab3.services;

import edu.asupoly.ser422.lab3.model.PhoneEntry;
//import edu.asupoly.ser422.lab3.model.PhoneBook;

import java.util.List;

// we'll build on this later
public interface PhoneBookService {
	// PhoneBook methods
    List<PhoneEntry> getAllEntriesFromPhoneBook(String phoneBookID);

    // Phone Entry methods
    PhoneEntry getPhoneEntry(String phoneNumber);
    int createPhoneEntry(String phoneNumber, String firstName, String lastName, String phoneBookID);
    boolean updatePhoneEntryNames(PhoneEntry phoneEntry); //String phoneNubmer, String firstName, String lastName
    boolean updatePhoneBookToPhoneEntry(String phoneNumber, String id);
    boolean deletePhoneEntry(String phoneNumber);
    PhoneEntry getSubStringPhoneBookPhoneEntries(String firstName, String lastName);
    List<PhoneEntry> getUnlistedPhoneEntries();
}
