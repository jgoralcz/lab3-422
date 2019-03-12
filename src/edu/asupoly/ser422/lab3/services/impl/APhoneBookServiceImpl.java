package edu.asupoly.ser422.lab3.services.impl;

import java.util.Random;

public abstract class APhoneBookServiceImpl implements edu.asupoly.ser422.lab3.services.PhoneBookService {
	private static Random __r = new Random();
	/*
	 * A simple method to generate a random key. Now this can cause clashes, but I am leaving it
	 * in so you have a random error generator (and should think about how to handle that).
	 */
	protected int generateKey(int lb, int ub) {
		return __r.nextInt(Math.abs(ub-lb)) + ((ub>lb) ? lb : ub);
	}
	
}