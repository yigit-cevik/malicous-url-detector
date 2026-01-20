package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import database.Borrows;

public class Mainframe {
	public Connection getConnected() throws SQLException {

		return DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "21802180");

	}

	public ArrayList<Books> getBooks() throws SQLException {
		ArrayList<Books> temp_books = new ArrayList<>();
		Statement st = getConnected().createStatement();
		ResultSet rs = st.executeQuery("select * from books");
		while (rs.next()) {
			Books books = new Books();
			books.setBookID(rs.getInt(1));
			books.setTitle(rs.getString(2));
			books.setAuthor(rs.getString(3));
			books.setCopies(rs.getInt(4));
			books.setCategory(rs.getString(5));
			temp_books.add(books);

		}
		return temp_books;
	}

	public ArrayList<Members> getMembers() throws SQLException {
		ArrayList<Members> temp_members = new ArrayList<>();
		Statement st = getConnected().createStatement();
		ResultSet rs = st.executeQuery("select * from members");
		while (rs.next()) {
			Members members = new Members();
			members.setMemberID(rs.getInt(1));
			members.setName(rs.getString(2));

		}
		return temp_members;
	}

	public ArrayList<Borrows> getBorrows() throws SQLException {
		ArrayList<Borrows> temp_borrows = new ArrayList<>();
		Statement st = getConnected().createStatement();
		ResultSet rs = st.executeQuery("select * from borrows");
		while (rs.next()) {
			Borrows borrows = new Borrows();
			borrows.setBorrowID(rs.getInt(1));
			borrows.setBook(rs.getInt(2));
			borrows.setMember(rs.getInt(3));
			borrows.setBorrowDate(rs.getString(4));
			borrows.setReturnDate(rs.getString(5));
           
			
			
			temp_borrows.add(borrows);
		
			

		}
		return temp_borrows;
	}
	
	
	public ArrayList<Books> getBooksByCategory(String category) throws SQLException {
		ArrayList<Books> booksList = new ArrayList<>();
		String query = "SELECT * FROM books WHERE category = ? ORDER BY title ASC";
		PreparedStatement ps = getConnected().prepareStatement(query);
		ps.setString(1, category);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Books book = new Books();
			book.setBookID(rs.getInt("bookID"));
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setCopies(rs.getInt("copies"));
			book.setCategory(rs.getString("category"));
			booksList.add(book);
		}
		return booksList;
	}

	
	
	
	public void deleteborrows(int id) throws SQLException {
		String query = "delete from borrows where BorrowID=?";
		PreparedStatement ps = getConnected().prepareStatement(query);
		ps.setInt(1, id);
		ps.executeUpdate();
	}
	
	public void saveborrows(Borrows brw) throws SQLException {
		String query = "insert into borrows values(?,?,?,?,?)";
		PreparedStatement ps = getConnected().prepareStatement(query);
		ps.setInt(1, brw.getBorrowID());
		ps.setInt(2, brw.getBook());
		ps.setInt(3, brw.getMember());
		ps.setString(4, brw.getBorrowDate());
		ps.setString(5, brw.getReturnDate());
		ps.executeUpdate();
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
