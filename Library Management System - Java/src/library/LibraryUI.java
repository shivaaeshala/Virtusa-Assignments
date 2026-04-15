package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LibraryUI extends JFrame {

    private LibraryService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public LibraryUI() {
        service = new LibraryService();

        setTitle("Library Management System");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Books", createBooksPanel());
        tabs.add("Users", createUsersPanel());
        tabs.add("Records", createRecordsPanel());

        add(tabs);
        setVisible(true);
    }

    // ---------------- BOOK PANEL ----------------
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Available"}, 0);
        table = new JTable(tableModel);

        JButton addBtn = new JButton("Add Book");
        JButton deleteBtn = new JButton("Delete Book");
        JButton refreshBtn = new JButton("Refresh");

        JPanel top = new JPanel();
        JTextField title = new JTextField(10);
        JTextField author = new JTextField(10);

        top.add(new JLabel("Title:"));
        top.add(title);
        top.add(new JLabel("Author:"));
        top.add(author);
        top.add(addBtn);

        JPanel bottom = new JPanel();
        bottom.add(deleteBtn);
        bottom.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                service.addBook(title.getText(), author.getText());
                loadBooks();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                try {
                    service.deleteBook(id);
                    loadBooks();
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        refreshBtn.addActionListener(e -> loadBooks());

        loadBooks();
        return panel;
    }

    // ---------------- USERS PANEL ----------------
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel userModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0);
        JTable userTable = new JTable(userModel);

        JTextField name = new JTextField(10);
        JTextField email = new JTextField(10);

        JButton add = new JButton("Add User");
        JButton delete = new JButton("Delete User");
        JButton refresh = new JButton("Refresh");

        JPanel top = new JPanel();
        top.add(new JLabel("Name:"));
        top.add(name);
        top.add(new JLabel("Email:"));
        top.add(email);
        top.add(add);

        JPanel bottom = new JPanel();
        bottom.add(delete);
        bottom.add(refresh);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        add.addActionListener(e -> {
            try {
                service.addUser(name.getText(), email.getText());
                loadUsers(userModel);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        delete.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                int id = (int) userModel.getValueAt(row, 0);
                try {
                    service.deleteUser(id);
                    loadUsers(userModel);
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        refresh.addActionListener(e -> loadUsers(userModel));

        loadUsers(userModel);
        return panel;
    }

    // ---------------- RECORDS PANEL ----------------
    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Book", "User", "Issue Date", "Due Date", "Return Date"}, 0
        );

        JTable recordTable = new JTable(model);

        JButton refresh = new JButton("Refresh");

        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadRecords(model));

        loadRecords(model);
        return panel;
    }

    // ---------------- LOAD DATA ----------------
    private void loadBooks() {
        try {
            tableModel.setRowCount(0);
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT * FROM books");

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available") ? "Yes" : "No"
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadUsers(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT * FROM users");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email")
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadRecords(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            ResultSet rs = service.getRecords();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("record_id"),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date")
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}