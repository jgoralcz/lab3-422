package edu.asupoly.ser422.lab3.services;

import java.util.Properties;

//import edu.asupoly.ser422.lab3.model.PhoneEntry;
//import edu.asupoly.ser422.lab3.services.impl.SimplePhoneBookServiceImpl;

// we'll build on this later
public class PhoneBookServiceFactory {
    private PhoneBookServiceFactory() {}

    final static String propertiesFile = "/lab3.properties";

    public static PhoneBookService getInstance() {
	// should really read from a property here
	if (__phonebookService == null) {
		__phonebookService = new SimplePhoneBookServiceImpl();
	}
	return __phonebookService;
    }

    private static PhoneBookService __phonebookService;
    
	// This class is going to look for a file named lab3.properties in the classpath
	// to get its initial settings
	static {
		try {
			Properties dbProperties = new Properties();
			Class<?> initClass = null;
			dbProperties.load(edu.asupoly.ser422.lab3.services.PhoneBookServiceFactory.class.getClassLoader().getResourceAsStream(propertiesFile));
			String serviceImpl = dbProperties.getProperty("serviceImpl");
			if (serviceImpl != null) {
				initClass = Class.forName(serviceImpl);
			} else {
				initClass = Class.forName("edu.asupoly.ser422.lab3.services.impl.SimplePhoneBookService");
			}
			__phonebookService = (PhoneBookService) initClass.newInstance();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {

		}
	}
    
}

//	PhoneEntry pentry = new PhoneEntry(req.getParameter("firstname"),
//			req.getParameter("lastname"), req.getParameter("phone"));
