package library;

import java.sql.*;
import java.time.LocalDate;

public class LibraryService {

    private static final int ISSUE_DAYS = 14;
    private static final int FINE_PER_DAY = 5;

    public void addBook(String title, String author) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO books(title, author, available) VALUES (?, ?, true)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, author);
        ps.executeUpdate();
        System.out.println("Book added successfully.");
        conn.close();
    }

    public void viewBooks() throws Exception {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM books");

        while (rs.next()) {
            System.out.println(
                rs.getInt("book_id") + " | " +
                rs.getString("title") + " | " +
                rs.getString("author") + " | " +
                (rs.getBoolean("available") ? "Available" : "Not Available")
            );
        }
        conn.close();
    }

    public void registerUser(String name, String email) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO users(name, email) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, email);
        ps.executeUpdate();
        System.out.println("User registered successfully.");
        conn.close();
    }

    public void issueBook(int bookId, int userId) throws Exception {
        Connection conn = DBConnection.getConnection();

        // check availability
        PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE book_id=?");
        check.setInt(1, bookId);
        ResultSet rs = check.executeQuery();

        if (!rs.next() || !rs.getBoolean("available")) {
            System.out.println("Book not available.");
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
        ps.setDate(3, Date.valueOf(issueDate));
        ps.setDate(4, Date.valueOf(dueDate));
        ps.executeUpdate();

        PreparedStatement update = conn.prepareStatement("UPDATE books SET available=false WHERE book_id=?");
        update.setInt(1, bookId);
        update.executeUpdate();

        System.out.println("Book issued successfully.");
        conn.close();
    }

    public void returnBook(int recordId) throws Exception {
        Connection conn = DBConnection.getConnection();

        PreparedStatement ps = conn.prepareStatement(
            "SELECT book_id, due_date FROM records WHERE record_id=? AND return_date IS NULL"
        );
        ps.setInt(1, recordId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("Invalid record.");
            conn.close();
            return;
        }

        int bookId = rs.getInt("book_id");
        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
        LocalDate returnDate = LocalDate.now();

        long fine = 0;
        if (returnDate.isAfter(dueDate)) {
            fine = (returnDate.toEpochDay() - dueDate.toEpochDay()) * FINE_PER_DAY;
        }

        PreparedStatement updateRecord = conn.prepareStatement(
            "UPDATE records SET return_date=? WHERE record_id=?"
        );
        updateRecord.setDate(1, Date.valueOf(returnDate));
        updateRecord.setInt(2, recordId);
        updateRecord.executeUpdate();

        PreparedStatement updateBook = conn.prepareStatement(
            "UPDATE books SET available=true WHERE book_id=?"
        );
        updateBook.setInt(1, bookId);
        updateBook.executeUpdate();

        System.out.println("Book returned. Fine: Rs. " + fine);
        conn.close();
    }

    public void deleteBook(int bookId) throws Exception {
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id=?");
    ps.setInt(1, bookId);
    ps.executeUpdate();
    conn.close();
}

    public void addUser(String name, String email) throws Exception {
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO users(name, email) VALUES (?, ?)"
    );
    ps.setString(1, name);
    ps.setString(2, email);
    ps.executeUpdate();
    conn.close();
}

    public void deleteUser(int userId) throws Exception {
    Connection conn = DBConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(
        "DELETE FROM users WHERE user_id=?"
    );
    ps.setInt(1, userId);
    ps.executeUpdate();
    conn.close();
}

    public ResultSet getUsers() throws Exception {
    Connection conn = DBConnection.getConnection();
    Statement st = conn.createStatement();
    return st.executeQuery("SELECT * FROM users");
}

    public ResultSet getRecords() throws Exception {
    Connection conn = DBConnection.getConnection();
    Statement st = conn.createStatement();
    return st.executeQuery(
        "SELECT r.record_id, b.title, u.name, r.issue_date, r.due_date, r.return_date " +
        "FROM records r " +
        "JOIN books b ON r.book_id = b.book_id " +
        "JOIN users u ON r.user_id = u.user_id"
    );
}

    public String getBooksAsString() throws Exception {
    Connection conn = DBConnection.getConnection();
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery("SELECT * FROM books");

    StringBuilder sb = new StringBuilder();

    while (rs.next()) {
        sb.append(
            rs.getInt("book_id") + " | " +
            rs.getString("title") + " | " +
            rs.getString("author") + " | " +
            (rs.getBoolean("available") ? "Available" : "Not Available")
        ).append("\n");
    }

    conn.close();
    return sb.toString();
}
}