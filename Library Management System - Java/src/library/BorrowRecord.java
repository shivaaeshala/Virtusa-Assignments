package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private int recordId;
    private Book book;
    private User user;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowRecord(int recordId, Book book, User user, LocalDate issueDate, LocalDate dueDate) {
        this.recordId = recordId;
        this.book = book;
        this.user = user;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public int getRecordId() {
        return recordId;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public long calculateFine(int finePerDay) {
        if (returnDate == null) {
            return 0;
        }
        if (!returnDate.isAfter(dueDate)) {
            return 0;
        }
        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return lateDays * finePerDay;
    }

    @Override
    public String toString() {
        return "Record ID: " + recordId +
               ", Book: " + book.getTitle() +
               ", User: " + user.getName() +
               ", Issue Date: " + issueDate +
               ", Due Date: " + dueDate +
               ", Return Date: " + (returnDate == null ? "Not returned" : returnDate);
    }
}