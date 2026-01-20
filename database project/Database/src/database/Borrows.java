package database;




public class Borrows {
    private int borrowID;
    private int book;         
    private int member;       
    private String borrowDate;
    private String returnDate;

    public int getBorrowID() {
        return borrowID;
    }

    public void setBorrowID(int borrowID) {
        this.borrowID = borrowID;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int i) {
        this.book = i;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int i) {
        this.member = i;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}