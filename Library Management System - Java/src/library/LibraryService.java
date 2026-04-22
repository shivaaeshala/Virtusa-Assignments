package library;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class LibraryService {

    private static final int ISSUE_DAYS = 14;
    private static final int FINE_PER_DAY = 5;

    // ---------------- BOOKS ----------------
    public void addBook(Book book) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO books(title, author, available) VALUES (?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, book.getTitle());
        ps.setString(2, book.getAuthor());
        ps.setBoolean(3, book.isAvailable());

        ps.executeUpdate();
        conn.close();
    }

    public List<Book> getBooks() throws Exception {
        List<Book> list = new ArrayList<>();

        Connection conn = DBConnection.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM books");

        while (rs.next()) {
            Book b = new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author")
            );
            b.setAvailable(rs.getBoolean("available"));
            list.add(b);
        }

        conn.close();
        return list;
    }

    public void deleteBook(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        conn.close();
    }

    // ---------------- USERS ----------------
    public void addUser(User user) throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO users(name, email) VALUES (?, ?)"
        );

        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.executeUpdate();

        conn.close();
    }

    public List<User> getUsers() throws Exception {
        List<User> list = new ArrayList<>();

        Connection conn = DBConnection.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");

        while (rs.next()) {
            list.add(new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email")
            ));
        }

        conn.close();
        return list;
    }

    public void deleteUser(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM users WHERE user_id=?"
        );

        ps.setInt(1, id);
        ps.executeUpdate();
        conn.close();
    }

    // ---------------- ISSUE BOOK ----------------
    public void issueBook(int bookId, int userId) throws Exception {
        Connection conn = DBConnection.getConnection();

        ResultSet rs = conn.createStatement()
            .executeQuery("SELECT available FROM books WHERE book_id=" + bookId);

        if (!rs.next() || !rs.getBoolean("available")) {
            System.out.println("Book not available");
            conn.close();
            return;
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(ISSUE_DAYS);

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO records(book_id, user_id, issue_date, due_date) VALUES (?, ?, ?, ?)"
        );

        ps.setInt(1, bookId);
        ps.setInt(2, userId);
        ps.setDate(3, java.sql.Date.valueOf(issueDate));
        ps.setDate(4, java.sql.Date.valueOf(dueDate));
        ps.executeUpdate();

        conn.createStatement().executeUpdate(
            "UPDATE books SET available=false WHERE book_id=" + bookId
        );

        conn.close();
    }

    // ---------------- RETURN BOOK ----------------
    public void returnBook(int recordId) throws Exception {
        Connection conn = DBConnection.getConnection();

        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT * FROM records WHERE record_id=" + recordId + " AND return_date IS NULL"
        );

        if (!rs.next()) {
            System.out.println("Invalid record");
            conn.close();
            return;
        }

        int bookId = rs.getInt("book_id");
        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
        LocalDate returnDate = LocalDate.now();

        BorrowRecord record = new BorrowRecord(
            recordId,
            null,
            null,
            rs.getDate("issue_date").toLocalDate(),
            dueDate
        );

        record.setReturnDate(returnDate);

        long fine = record.calculateFine(FINE_PER_DAY);

        PreparedStatement ps = conn.prepareStatement(
            "UPDATE records SET return_date=? WHERE record_id=?"
        );

        ps.setDate(1, java.sql.Date.valueOf(returnDate));
        ps.setInt(2, recordId);
        ps.executeUpdate();

        conn.createStatement().executeUpdate(
            "UPDATE books SET available=true WHERE book_id=" + bookId
        );

        System.out.println("Fine: Rs. " + fine);

        conn.close();
    }

    // ---------------- RECORDS ----------------
    public List<BorrowRecord> getRecords() throws Exception {
        List<BorrowRecord> list = new ArrayList<>();

        Connection conn = DBConnection.getConnection();

        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT r.*, b.title, b.author, u.name, u.email " +
            "FROM records r " +
            "JOIN books b ON r.book_id = b.book_id " +
            "JOIN users u ON r.user_id = u.user_id"
        );

        while (rs.next()) {
            Book book = new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author")
            );

            User user = new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email")
            );

            BorrowRecord record = new BorrowRecord(
                rs.getInt("record_id"),
                book,
                user,
                rs.getDate("issue_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate()
            );

            if (rs.getDate("return_date") != null) {
                record.setReturnDate(rs.getDate("return_date").toLocalDate());
            }

            list.add(record);
        }

        conn.close();
        return list;
    }
}