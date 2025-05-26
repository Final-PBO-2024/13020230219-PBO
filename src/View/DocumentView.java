package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DocumentView {
    private JFrame documentFrame;
    private JButton backButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton accessButton;
    private JButton logoutButton; // Tambahkan variabel untuk tombol Keluar
    private JTable table;
    private DefaultTableModel tableModel; // Model tabel dinamis
    private JButton friendsButton;
    private JButton loginHistoryButton;
    private JButton userButton;
    private JButton settingsButton;
    private JButton profileButton;

    public DocumentView() {
        System.out.println("Konstruktor DocumentView dipanggil");
        documentFrame = new JFrame("Kelola Dokumen");
        documentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        documentFrame.setSize(800, 500);
        documentFrame.setLocationRelativeTo(null);
        documentFrame.setLayout(new BorderLayout(10, 10));

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 16));
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(adminLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        backButton = new JButton("DASHBOARD");
        backButton.setBackground(new Color(30, 30, 30));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(backButton);

        friendsButton = new JButton("List Teman");
        friendsButton.setBackground(new Color(30, 30, 30));
        friendsButton.setForeground(Color.WHITE);
        friendsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        friendsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(friendsButton);

        loginHistoryButton = new JButton("Riwayat Login");
        loginHistoryButton.setBackground(new Color(30, 30, 30));
        loginHistoryButton.setForeground(Color.WHITE);
        loginHistoryButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginHistoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(loginHistoryButton);

        userButton = new JButton("User");
        userButton.setBackground(new Color(30, 30, 30));
        userButton.setForeground(Color.WHITE);
        userButton.setFont(new Font("Arial", Font.PLAIN, 14));
        userButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userButton);

        settingsButton = new JButton("Pengaturan");
        settingsButton.setBackground(new Color(30, 30, 30));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(settingsButton);

        profileButton = new JButton("Profile");
        profileButton.setBackground(new Color(30, 30, 30));
        profileButton.setForeground(Color.WHITE);
        profileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        profileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(profileButton);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        logoutButton = new JButton("Keluar");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoutButton);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Kelola Dokumen");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Nama File", "Email", "Waktu Modifikasi"};
        tableModel = new DefaultTableModel(columnNames, 0); // Model dinamis
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Tambah File");
        updateButton = new JButton("Update File");
        deleteButton = new JButton("Hapus File");
        accessButton = new JButton("Beri Akses");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(accessButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        documentFrame.add(sidebar, BorderLayout.WEST);
        documentFrame.add(contentPanel, BorderLayout.CENTER);
        System.out.println("Konstruktor DocumentView selesai");
    }

    // Metode untuk menambahkan dokumen ke tabel
    public void addDocument(String name, String email, String timestamp) {
        tableModel.addRow(new Object[]{name, email, timestamp});
    }

    // Metode untuk menghapus dokumen dari tabel
    public void removeDocument(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tableModel.getRowCount()) {
            tableModel.removeRow(rowIndex);
        }
    }

    public JFrame getDocumentFrame() { return documentFrame; }
    public JButton getBackButton() { return backButton; }
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getAccessButton() { return accessButton; }
    public JButton getLogoutButton() { return logoutButton; } // Getter untuk tombol Keluar
    public JTable getTable() { return table; }
    public JButton getFriendsButton() { return friendsButton; }
    public JButton getLoginHistoryButton() { return loginHistoryButton; }
    public JButton getUserButton() { return userButton; }
    public JButton getSettingsButton() { return settingsButton; }
    public JButton getProfileButton() { return profileButton; }
}