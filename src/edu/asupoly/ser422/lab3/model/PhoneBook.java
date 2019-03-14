package edu.asupoly.ser422.lab3.model;

import java.util.ArrayList;
import java.util.List;

public class PhoneBook {

	private List<PhoneEntry> _pbook = new ArrayList<>();

	public PhoneBook() { }

	public void addPhoneEntry(PhoneEntry pe) {
		_pbook.add(pe);
	}

	public List<PhoneEntry> getBook () {
		return _pbook;
	}

	public int size() {
		return _pbook.size();
	}
}
