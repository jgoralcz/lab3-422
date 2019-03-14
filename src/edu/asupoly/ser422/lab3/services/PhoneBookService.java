package edu.asupoly.ser422.lab3.services;

import edu.asupoly.ser422.lab3.model.PhoneBook;
import edu.asupoly.ser422.lab3.model.PhoneEntry;

// we'll build on this later
public interface PhoneBookService {
	// PhoneBook methods
    PhoneBook getAllEntriesFromPhoneBook(String phoneBookID);

    // Phone Entry methods
    PhoneEntry getPhoneEntry(String phoneNumber);
    int createPhoneEntry(String phoneNumber, String firstName, String lastName, String phoneBookID);
    boolean updatePhoneEntryNames(PhoneEntry phoneEntry); //String phoneNubmer, String firstName, String lastName
    boolean updatePhoneBookToPhoneEntry(String phoneNumber, String id);
    boolean deletePhoneEntry(String phoneNumber);
    PhoneBook getSubStringPhoneBookPhoneEntries(String id, String firstName, String lastName);
    PhoneBook getUnlistedPhoneEntries();
}
