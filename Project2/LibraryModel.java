/*
 * LibraryModel.java
 * Author:
 * Created on:
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.swing.*;

public class LibraryModel {

	// For use in creating dialogs and making them modal
	private JFrame dialogParent;
	private Connection con = null;

	public LibraryModel(JFrame parent, String userid, String password) {
		dialogParent = parent;
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/" + userid + "_jdbc";
			con = DriverManager.getConnection(url, userid, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String bookLookup(int isbn) {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		boolean bookEx = false;
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR  WHERE ISBN=" + isbn
					+ "ORDER BY AuthorSeqNo ASC");
			while (rs.next()) {
				bookEx = true;
				String title = "\n title: " + rs.getString("Title");
				String edi = "\n edition_no: " + rs.getString("edition_no");
				String noc = "\n num_of-copy: " + rs.getString("numofcop");
				String nl = "\n num left: " + rs.getString("numleft");
				String Author = "\n Author: " + rs.getString("name");
				if (result.equals("")) {
					result += "\n \n isbn: " + isbn + title + edi + noc + nl + Author;
				} else {
					result += Author;
				}
			}
			if (!bookEx) {
				result = "The book is not exist";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String showCatalogue() {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM book ORDER BY isbn ASC");
			while (rs.next()) {
				String isbn = "isbn: " + rs.getString("isbn");
				String title = "\n title: " + rs.getString("Title");
				String edi = "\n edition_no: " + rs.getString("edition_no");
				String noc = "\n num_of-copy: " + rs.getString("numofcop");
				String nl = "\n num left: " + rs.getString("numleft");
				result += "\n \n" + isbn + title + edi + noc + nl;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String showLoanedBooks() {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		String L = "";
		boolean loaned = false;
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM book WHERE numofcop > numLeft ORDER BY isbn ASC");
			while (rs.next()) {
				loaned = true;
				L = "Loaned books : \n";
				String title = "\n title: " + rs.getString("Title");
				String edi = "\n edition_no: " + rs.getString("edition_no");
				String noc = "\n num_of-copy: " + rs.getString("numofcop");
				String nl = "\n num left: " + rs.getString("numleft");
				String isbn = "isbn: " + rs.getString("isbn");
				result += "\n \n " + isbn + title + edi + noc + nl;
			}
			if (!loaned) {
				L = "There is no loaned books";

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return L + result;
	}

	public String showAuthor(int authorID) {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		boolean AEx = false;
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM Author WHERE authorid=" + authorID);
			while (rs.next()) {
				AEx = true;
				String AuthorName = "\n Author Name: " + rs.getString("name");
				String AuthorS = "\n Author Sur name: " + rs.getString("surname");
				result += "\n \n Author ID: " + authorID + AuthorName + AuthorS;
			}
			if (!AEx) {
				result = "The Author is not exist";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String showAllAuthors() {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM Author");
			while (rs.next()) {
				String AuthorName = "\n Author Name: " + rs.getString("name");
				String AuthorS = "\n Author Sur name: " + rs.getString("surname");
				String AuthorID = "\n Author ID: " + rs.getString("authorid");
				result += "\n \n Author ID: " + AuthorID + AuthorName + AuthorS;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String showCustomer(int customerID) {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		boolean CusEx = false;
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
			while (rs.next()) {
				CusEx = true;
				String CName = "\n Customer Name: " + rs.getString("F_Name") + rs.getString("L_Name");
				String City = "\n City: " + rs.getString("city");
				result += "\n \n Customer ID: " + customerID + CName + City;
			}
			if (!CusEx) {
				result = "The customer is not exist";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String showAllCustomers() {
		Statement s = null;
		ResultSet rs = null;
		String result = "";
		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM Customer");
			while (rs.next()) {
				String CName = "\n Customer Name: " + rs.getString("F_Name") + rs.getString("L_Name");
				String City = "\n City: " + rs.getString("city");
				String CustomerID = "\n City: " + rs.getString("customerId");
				result += "\n \n Customer ID: " + CustomerID + CName + City;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String borrowBook(int isbn, int customerID, int day, int month, int year) {
		Statement s = null;
		String result = "";
		try {
			s = con.createStatement();
			// check if customer exist
			ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
			s = con.createStatement();
			// check if customer exist

			if (rsCustomer.next()) {
				s = con.createStatement();
				s.execute("BEGIN");
				s.execute("LOCK Customer IN ROW SHARE MODE;");
				ResultSet rsbook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
				if (rsbook.next()) {
					String bookleft = rsbook.getString("numLeft");
					if (Integer.parseInt(bookleft) > 0) {
						s.execute("LOCK book IN ROW SHARE MODE;");

						JFrame f = new JFrame();
						int a = JOptionPane.showConfirmDialog(f, "Are you sure that you want to borrow this book?");
						if (a == JOptionPane.YES_OPTION) {
							f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							LocalDate date = LocalDate.of(year, month, day);
							s.executeUpdate("INSERT INTO cust_book VALUES('" + isbn + "','" + date + "','" + customerID
									+ "');");
							s.executeUpdate("UPDATE book SET numleft = numleft-1 WHERE isbn =" + isbn + " ;");
							s.execute("commit;");
							result = "book borrowed ";
						}

						f.setSize(300, 300);
						f.setLayout(null);
					}
				} else {
					result = "there are no book left";
				}
			} else {
				result = "the customer does not exist";
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String returnBook(int isbn, int customerid) {
		Statement s = null;
		String result = "";
		try {
			s = con.createStatement();
			// check if customer exist
			ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerid);
			s = con.createStatement();
			// check if customer exist

			if (rsCustomer.next()) {
				s = con.createStatement();
				s.execute("BEGIN");
				s.execute("LOCK Customer IN ROW SHARE MODE;");
				ResultSet rsbook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
				if (rsbook.next()) {
					String bookleft = rsbook.getString("numLeft");
					if (Integer.parseInt(bookleft) > 0) {
						s.execute("LOCK book IN ROW SHARE MODE;");

						JFrame f = new JFrame();
						int a = JOptionPane.showConfirmDialog(f, "Are you sure that you want to return this book?");
						if (a == JOptionPane.YES_OPTION) {
							f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							s.executeUpdate("DELETE FROM cust_book WHERE customerid =" + customerid + ";");
							s.executeUpdate("UPDATE book SET numleft = numleft+1 WHERE isbn =" + isbn + " ;");
							s.execute("commit;");
							result = "book returned. ";
						}

						f.setSize(300, 300);
						f.setLayout(null);
					}
				} else {
					result = "there are no book left";
				}
			} else {
				result = "the customer does not exist";
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void closeDBConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String deleteCus(int customerID) {
		Statement s = null;
		String result = "";
		try {
			s = con.createStatement();
			// check if customer exist
			ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
			if (rsCustomer.next()) {
				ResultSet rsCustCustomer = s.executeQuery("SELECT * FROM cust_book WHERE customerid=" + customerID);
				if (rsCustCustomer.next()) {
					s = con.createStatement();
					s.execute("BEGIN");
					result = "The customer with customerID(" + customerID
							+ ") still have loaned books not returned  \n So it cannot be deleted";
				} else {
					s.executeUpdate("DELETE FROM customer WHERE customerid = " + customerID);
					s.execute("commit;");
					result = "The customer deleted";
				}

			} else {
				result = "the customer does not exist";
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String deleteAuthor(int authorID) {
		Statement s = null;
		String result = "";
		try {
			s = con.createStatement();
			// check if customer exist
			ResultSet rsAuthor = s.executeQuery("SELECT * FROM Author WHERE authorid=" + authorID);
			if (rsAuthor.next()) {
				s.executeUpdate("DELETE FROM Book_Author WHERE authorid = " + authorID);
				s.executeUpdate("DELETE FROM Author WHERE authorid = " + authorID);
				s.execute("commit;");
				result = "The author deleted";

			} else {
				result = "the Author does not exist";
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String deleteBook(int isbn) {
		Statement s = null;
		String result = "";
		try {
			s = con.createStatement();
			// check if customer exist
			ResultSet rsBook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
			if (rsBook.next()) {
				ResultSet rsCustBook = s.executeQuery("SELECT * FROM cust_book WHERE isbn=" + isbn);
				if (rsCustBook.next()) {
					s = con.createStatement();
					s.execute("BEGIN");
					result = "The book with isbn(" + isbn
							+ ") still have loaned copies not returned  \n So it cannot be deleted";
				} else {
					ResultSet rsauBook = s.executeQuery("SELECT * FROM Book_Author WHERE isbn=" + isbn);
					s.executeUpdate("DELETE FROM Book_Author WHERE isbn = " + isbn);
					s.executeUpdate("DELETE FROM book WHERE isbn = " + isbn);

					s.execute("commit;");
					result = "The book deleted";
				}

			} else {
				result = "the book does not exist";
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}