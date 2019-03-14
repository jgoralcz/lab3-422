package edu.asupoly.ser422.lab3.services.impl;

import edu.asupoly.ser422.lab3.model.PhoneEntry;
import edu.asupoly.ser422.lab3.services.PhoneBookService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//A simple impl of interface PhoneBookService
public class RDBMPhoneBookServiceImpl implements PhoneBookService {
	private static Properties __dbProperties;
	private static String __jdbcUrl;
	private static String __jdbcUser;
	private static String __jdbcPasswd;
	private static String __jdbcDriver;
	
	private Connection getConnection() throws Exception {
		try {
			Class.forName(__jdbcDriver);
			return DriverManager.getConnection(__jdbcUrl, __jdbcUser, __jdbcPasswd);
		} catch (Exception exc) {
			throw exc;
		}
	}

	// Only instantiated by factory within package scope
	public RDBMPhoneBookServiceImpl() {

	}

	public List<PhoneEntry> getAllEntriesFromPhoneBook(String phoneBookID) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<PhoneEntry> rval = new ArrayList<>();
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			rs = stmt.executeQuery(__dbProperties.getProperty("sql.getAllEntriesFromPhoneBook"));
			while (rs.next()) {
				rval.add(new PhoneEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			}
		}
		catch (Exception se) {
			se.printStackTrace();
			return null;
		}
		finally {  // why nest all of these try/finally blocks?
			try {
				if (rs != null) { rs.close(); }
			} catch (Exception e1) { e1.printStackTrace(); }
			finally {
				try {
					if (stmt != null) { stmt.close(); }
				} catch (Exception e2) { e2.printStackTrace(); }
				finally {
					try {
						if (conn != null) { conn.close(); }
					} catch (Exception e3) { e3.printStackTrace(); }
				}
			}
		}

		return rval;
	}

	public int createPhoneEntry(String phoneNumber, String phoneBookID, String firstName, String lastName) {
		if (lastName == null || firstName == null || lastName.length() == 0 || firstName.length() == 0) {
			return -1;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.createPhoneEntry"));
//			int generatedKey = generateKey(1, 99999);
			stmt.setString(1, phoneNumber);
			stmt.setString(2, phoneBookID);
			stmt.setString(3, lastName);
			stmt.setString(4, firstName);
			// return stmt.executeUpdate();
			int updatedRows = stmt.executeUpdate();
			return -1; //TODO: return an int
//			if(updatedRows > 0) {
//				return generatedKey;
//			}else{
//				return -1;
//			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
			return -1;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	public boolean deletePhoneEntry(String phoneNumber) {
		boolean rval = false;
		Connection conn = null;
		PreparedStatement stmt  = null;
		PreparedStatement stmt2 = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.deletePhoneEntry"));
			stmt.setInt(1, Integer.parseInt(phoneNumber));
			if (rval = (stmt.executeUpdate() > 0)) {
				stmt2 = conn.prepareStatement(__dbProperties.getProperty("sql.deletePhoneEntryRefFromBook"));
				stmt2.setInt(1, Integer.parseInt(phoneNumber));
				stmt2.executeUpdate();
			}
			conn.commit();
			return rval;
		} catch (Exception sqe) {
			sqe.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
					if (stmt2 != null) { stmt2.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	@Override
	public PhoneEntry getPhoneEntry(String phoneNumber) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PhoneEntry phoneEntry = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.getPhoneEntry"));
			stmt.setInt(1, Integer.parseInt(phoneNumber));
			rs = stmt.executeQuery();
			if (rs.next()) {
				phoneEntry = new PhoneEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
		} finally {  // why nest all of these try/finally blocks?
			try {
				rs.close();
				if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
		return phoneEntry;
	}

	@Override
	public boolean updatePhoneEntryNames(PhoneEntry phoneEntry) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.updatePhoneEntry"));
			stmt.setString(1, phoneEntry.getFirstName());
			stmt.setString(2, phoneEntry.getLastName());
			stmt.setInt(3, Integer.parseInt(phoneEntry.getPhoneNumber()));
			return (stmt.executeUpdate() > 0);
		} catch (Exception sqe) {
			sqe.printStackTrace();
			return false;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	@Override
	public PhoneEntry getSubStringPhoneBookPhoneEntries(String firstName, String lastName) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PhoneEntry phoneEntry = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.getSubStringPhoneBookPhoneEntries"));
			stmt.setString(1, firstName);
			stmt.setString(2, lastName);
			rs = stmt.executeQuery();

			if (rs.next()) {
				phoneEntry = new PhoneEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}

		} catch (Exception sqe) {
			sqe.printStackTrace();
		} finally {  // why nest all of these try/finally blocks?
			try {
				rs.close();
				if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
		return phoneEntry;
	}

	@Override
	public PhoneEntry getUnlistedPhoneEntries() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PhoneEntry phoneEntry = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.getUnlistedPhoneEntries"));
			rs = stmt.executeQuery();
			if (rs.next()) {
				phoneEntry = new PhoneEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}

		} catch (Exception sqe) {
			sqe.printStackTrace();
		} finally {  // why nest all of these try/finally blocks?
			try {
				rs.close();
				if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
		return phoneEntry;
	}

	public boolean updatePhoneBookToPhoneEntry(String phoneNumber, String id) {
		boolean rval = false;
		Connection conn = null;
		PreparedStatement stmt  = null;
		PreparedStatement stmt2 = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.addPhoneBookToPhoneEntry"));
			stmt.setInt(1, Integer.parseInt(phoneNumber));
			stmt.setInt(1, Integer.parseInt(id));
			rval = stmt.executeUpdate() > 0;
			conn.commit();
			return rval;
		} catch (Exception sqe) {
			sqe.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		} finally {  // why nest all of these try/finally blocks?
			try {
				if (stmt != null) { stmt.close(); }
				if (stmt2 != null) { stmt2.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	// This class is going to look for a file named rdbm.properties in the classpath
	// to get its initial settings
	static {
		try {
			__dbProperties = new Properties();
			__dbProperties.load(RDBMPhoneBookServiceImpl.class.getClassLoader().getResourceAsStream("rdbm.properties"));
			__jdbcUrl    = __dbProperties.getProperty("jdbcUrl");
			__jdbcUser   = __dbProperties.getProperty("jdbcUser");
			__jdbcPasswd = __dbProperties.getProperty("jdbcPasswd");
			__jdbcDriver = __dbProperties.getProperty("jdbcDriver");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {

		}
	}
}
