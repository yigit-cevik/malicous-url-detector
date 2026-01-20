package database;

public class Books {
private int BookID;
private String Title;
private String Author;
private int Copies;
private String Category; // ✅ Yeni alan eklendi

public int getBookID() {
return BookID;
}

public void setBookID(int bookID) {
this.BookID = bookID;
}

public String getTitle() {
return Title;
}

public void setTitle(String title) {
this.Title = title;
}

public String getAuthor() {
return Author;
}

public void setAuthor(String author) {
this.Author = author;
}

public int getCopies() {
return Copies;
}

public void setCopies(int copies) {
Copies = copies;
}

// ✅ Yeni getter ve setter
public String getCategory() {
return Category;
}

public void setCategory(String category) {
this.Category = category;
}
}