package database;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ActionEvent;
import com.toedter.calendar.JDateChooser;

public class LibMainPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtMemberID;
	private JTextField txtName;
	DefaultListModel<String> model;
	Mainframe mf = new Mainframe();

	public void fillList() throws SQLException {
		model.removeAllElements();
		ArrayList<Borrows> borrows = mf.getBorrows();
		for (Borrows brw : borrows) {
			model.addElement(brw.getBorrowID() + " BookID: " + brw.getBook() + " MemberID: " + brw.getMember()
					+ " ---> " + brw.getBorrowDate() + " / " + brw.getReturnDate());
		}
	}

	private void loadBooksIntoComboBox(JComboBox<String> comboBox) {
		String url = "jdbc:mysql://localhost:3306/student";
		String user = "root";
		String password = "21802180";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, user, password);
			String sql = "SELECT bookID,title,copies,category FROM books";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int copies = rs.getInt("copies");
				int bookID = rs.getInt("bookID");
				String bookTitle = rs.getString("title");
				String category = rs.getString("category");
				comboBox.addItem(bookID + " - " + bookTitle + " |Category: " + category + " |Copies = " + copies);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading books from database.");
		}
	}

	private void loadCategoriesIntoComboBox(JComboBox<String> categoryBox) {
		String url = "jdbc:mysql://localhost:3306/student";
		String user = "root";
		String password = "21802180";
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT category FROM books");
				ResultSet rs = stmt.executeQuery()) {
			categoryBox.addItem("All");
			while (rs.next()) {
				categoryBox.addItem(rs.getString("category"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading categories.");
		}
	}

	private void loadBooksIntoComboBoxFiltered(JComboBox<String> comboBox, String category) {
		String url = "jdbc:mysql://localhost:3306/student";
		String user = "root";
		String password = "21802180";
		String sql = category.equals("All") ?
				"SELECT bookID, title, copies, category FROM books" :
				"SELECT bookID, title, copies, category FROM books WHERE category = ?";
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			if (!category.equals("All")) {
				stmt.setString(1, category);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int bookID = rs.getInt("bookID");
				String title = rs.getString("title");
				int copies = rs.getInt("copies");
				String cat = rs.getString("category");
				comboBox.addItem(bookID + " - " + title + " |Category: " + cat + " |Copies = " + copies);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading filtered books.");
		}
	}

	private void updateBookCopies(int bookID, int change) {
		String url = "jdbc:mysql://localhost:3306/student";
		String user = "root";
		String password = "21802180";
		String sql = "UPDATE books SET copies = copies + ? WHERE bookID = ?";
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, change);
			stmt.setInt(2, bookID);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error updating book copies.");
		}
	}

	private boolean isMemberValid(int memberID, String name) {
		String url = "jdbc:mysql://localhost:3306/student";
		String user = "root";
		String password = "21802180";
		String sql = "SELECT name FROM members WHERE memberID = ?";
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, memberID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String dbName = rs.getString("name");
				return dbName.equalsIgnoreCase(name.trim());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error validating member.");
		}
		return false;
	}

	private boolean hasBorrowedBefore(int memberID, int bookID) {
		String url = "jdbc:mysql://localhost:3306/student";
		String user = "root";
		String password = "21802180";
		String sql = "SELECT COUNT(*) FROM borrows WHERE memberID = ? AND bookID = ?";
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, memberID);
			stmt.setInt(2, bookID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error checking previous borrow.");
		}
		return false;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				LibMainPage frame = new LibMainPage();
				frame.setVisible(true);
				frame.fillList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public LibMainPage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1123, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblMemberID = new JLabel("MemberID");
		lblMemberID.setBounds(4, 35, 65, 27);
		contentPane.add(lblMemberID);

		JLabel lblName = new JLabel("Name");
		lblName.setBounds(10, 73, 46, 14);
		contentPane.add(lblName);

		model = new DefaultListModel<>();
		JList<String> list = new JList<>();
		list.setBounds(467, 40, 548, 249);
		list.setModel(model);
		list.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		contentPane.add(list);


		JComboBox<String> cbBooks = new JComboBox<>();
		cbBooks.setBounds(107, 224, 350, 22);
		contentPane.add(cbBooks);
		String initialCategory = "All";
		loadBooksIntoComboBoxFiltered(cbBooks, initialCategory);

		JLabel lblBooks = new JLabel("Books");
		lblBooks.setBounds(10, 228, 46, 14);
		contentPane.add(lblBooks);

		txtMemberID = new JTextField();
		txtMemberID.setBounds(107, 38, 76, 20);
		contentPane.add(txtMemberID);
		txtMemberID.setColumns(10);

		txtName = new JTextField();
		txtName.setBounds(107, 70, 76, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);

		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setBounds(109, 119, 120, 20);
		dateChooser.setDate(new Date());
		dateChooser.getDateEditor().setEnabled(false);
		dateChooser.setEnabled(false);
		contentPane.add(dateChooser);

		JDateChooser dateChooser_1 = new JDateChooser();
		dateChooser_1.setBounds(109, 150, 120, 19);
		contentPane.add(dateChooser_1);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();
		dateChooser_1.setMinSelectableDate(today);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(e -> {
			try {
				if (txtMemberID.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a MemberID.");
					return;
				}
				int memberID = Integer.parseInt(txtMemberID.getText().trim());
				String name = txtName.getText().trim();
				if (!isMemberValid(memberID, name)) {
					JOptionPane.showMessageDialog(null, "Member ID and name do not match or do not exist.");
					return;
				}
				String selectedBook = cbBooks.getSelectedItem().toString();
				int bookID = Integer.parseInt(selectedBook.split(" ")[0]);
				if (hasBorrowedBefore(memberID, bookID)) {
					JOptionPane.showMessageDialog(null, "This member has already borrowed this book.");
					return;
				}
				String copiesStr = selectedBook.substring(selectedBook.indexOf("Copies = ") + 9);
				int copies = Integer.parseInt(copiesStr.trim());
				if (copies == 0) {
					JOptionPane.showMessageDialog(null, "This book is currently unavailable (0 copies left).");
					return;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String borrowDateStr = sdf.format(dateChooser.getDate());
				String returnDateStr = sdf.format(dateChooser_1.getDate());

				Borrows bor = new Borrows();
				bor.setBorrowID(0);
				bor.setBook(bookID);
				bor.setMember(memberID);
				bor.setBorrowDate(borrowDateStr);
				bor.setReturnDate(returnDateStr);

				mf.saveborrows(bor);
				updateBookCopies(bookID, -1);
				cbBooks.removeAllItems();
				String selectedCategory = cbCategory.getSelectedItem().toString();
				loadBooksIntoComboBoxFiltered(cbBooks, selectedCategory);
				fillList();
				JOptionPane.showMessageDialog(null, "Record successfully added.");
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "There was an error in recording.");
			}
		});
		btnSave.setBounds(10, 266, 89, 23);
		contentPane.add(btnSave);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> {
			try {
				java.util.List<String> selectedBorrows = list.getSelectedValuesList();
				if (selectedBorrows.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please select at least one record to delete.");
					return;
				}

				for (String selected : selectedBorrows) {
					String[] parts = selected.split(" ");
					int borrow_id = Integer.parseInt(parts[0]);
					int book_id = Integer.parseInt(parts[2]);

					mf.deleteborrows(borrow_id);
					updateBookCopies(book_id, 1);
				}

				fillList();
				cbBooks.removeAllItems();
				String selectedCategory = cbCategory.getSelectedItem().toString();
				loadBooksIntoComboBoxFiltered(cbBooks, selectedCategory);
				JOptionPane.showMessageDialog(null, "Selected records deleted.");
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Delete operation failed.");
			}
		});
		btnDelete.setBounds(147, 266, 89, 23);
		contentPane.add(btnDelete);

		JLabel lblBorrowDate = new JLabel("Borrow Date");
		lblBorrowDate.setBounds(4, 116, 95, 14);
		contentPane.add(lblBorrowDate);

		JLabel lblReturnDate = new JLabel("Return Date");
		lblReturnDate.setBounds(4, 147, 95, 14);
		contentPane.add(lblReturnDate);

		JLabel lblCategory = new JLabel("Category");
		lblCategory.setBounds(4, 192, 95, 14);
		contentPane.add(lblCategory);

		cbCategory = new JComboBox<>();
		cbCategory.setBounds(107, 191, 350, 22);
		contentPane.add(cbCategory);
		loadCategoriesIntoComboBox(cbCategory);
		cbCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCategory = cbCategory.getSelectedItem().toString();
				cbBooks.removeAllItems();
				loadBooksIntoComboBoxFiltered(cbBooks, selectedCategory);
			}
		});
	}

	private JComboBox<String> cbCategory;
}
