package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class DashboardView {
    private JFrame dashboardFrame;
    private JButton logoutButton;
    private JButton documentButton;
    private JButton friendsButton;
    private JButton loginHistoryButton;
    private JButton userButton;
    private JButton settingsButton;
    private JButton profileButton;
    private JButton refreshButton; // Tombol baru untuk menyegarkan tabel
    private JTable activityTable; // Tabel untuk riwayat aktivitas
    private DefaultTableModel tableModel; // Model tabel yang dapat diubah
    private JLabel userLabel; // Label untuk menampilkan username

    public DashboardView(String username) {
        dashboardFrame = new JFrame("Dashboard");
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setSize(800, 500);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setLayout(new BorderLayout(10, 10));
        dashboardFrame.setVisible(false);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        userLabel = new JLabel("Selamat datang, " + username); // Menampilkan username
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 16));
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(adminLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        documentButton = new JButton("Kelola Dokumen");
        documentButton.setBackground(new Color(30, 30, 30));
        documentButton.setForeground(Color.WHITE);
        documentButton.setFont(new Font("Arial", Font.PLAIN, 14));
        documentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(documentButton);

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

        // Tombol Refresh
        refreshButton = new JButton("Segarkan");
        refreshButton.setBackground(new Color(30, 30, 30));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(refreshButton);

        // Panel Konten
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.add(createInfoCard("Jumlah Dokumen", "2"));
        headerPanel.add(createInfoCard("Access", "2"));
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableTitle = new JLabel("Riwayat Aktivitas");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Inisialisasi tabel dengan model dinamis
        String[] columnNames = {"Aktivitas", "Tanggal", "Waktu"};
        tableModel = new DefaultTableModel(columnNames, 0); // Model kosong
        activityTable = new JTable(tableModel);
        activityTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Data statis awal (bisa diganti oleh controller)
        addActivity("Menambah File", "2025-04-23", "23:00");
        addActivity("Mengubah Isi 'excel.xlsx'", "2025-04-23", "23:00");

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        dashboardFrame.add(sidebar, BorderLayout.WEST);
        dashboardFrame.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createInfoCard(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    // Metode untuk menambahkan aktivitas ke tabel (dapat dipanggil oleh controller)
    public void addActivity(String activity, String date, String time) {
        tableModel.addRow(new Object[]{activity, date, time});
    }

    // Metode untuk menyegarkan tabel (kosongkan dan isi ulang)
    public void refreshTable() {
        tableModel.setRowCount(0); // Kosongkan tabel
        // Tambahkan data awal kembali (bisa diganti dengan data dinamis)
        addActivity("Menambah File", "2025-04-23", "23:00");
        addActivity("Mengubah Isi 'excel.xlsx'", "2025-04-23", "23:00");
    }

    public JFrame getDashboardFrame() {
        return dashboardFrame;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JButton getDocumentButton() {
        return documentButton;
    }

    public JButton getFriendsButton() {
        return friendsButton;
    }

    public JButton getLoginHistoryButton() {
        return loginHistoryButton;
    }

    public JButton getUserButton() {
        return userButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getProfileButton() {
        return profileButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }
}