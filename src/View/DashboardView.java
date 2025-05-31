package View;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; // Untuk header renderer
import javax.swing.UIManager; // Untuk default border header


public class DashboardView {
    private JFrame dashboardFrame;
    private JButton logoutButton;
    private JButton documentButton;
    private JButton recycleBinButton;
    private JButton refreshButton;
    private JButton profileButton; // Tombol Profile
    private JButton sharedDocumentsButton; // Tombol Shared Documents

    private JLabel userLabel;
    private JLabel cardJumlahDokumenValue;
    private JLabel cardAccessValue;

    private JPanel mainContentArea;
    private CardLayout cardLayout;

    private JPanel activityPanel;
    private JTable activityTable;
    private DefaultTableModel activityTableModel;

    private JPanel recycleBinPanel;
    private JTable recycleBinTable;
    private DefaultTableModel recycleBinTableModel;
    private JButton restoreButton;
    private JButton permanentDeleteButton;

    // Konstanta Warna Baru (terinspirasi gambar Anda)
    private final Color COLOR_SIDEBAR_BG_NEW = new Color(68, 78, 96); 
    private final Color COLOR_SIDEBAR_BUTTON_BG_NEW = new Color(238, 238, 238); 
    private final Color COLOR_SIDEBAR_BUTTON_HOVER_BG_NEW = new Color(220, 220, 225); 
    private final Color COLOR_SIDEBAR_BUTTON_TEXT_NEW = new Color(50, 50, 70); 
    private final Color COLOR_SIDEBAR_USER_LABEL_TEXT = Color.WHITE;

    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_BUTTON_TEXT_DARK = new Color(52, 58, 64); 

    private final Color COLOR_PRIMARY_ACTION_BG = new Color(0, 123, 255);
    private final Color COLOR_SUCCESS_ACTION_BG = new Color(40, 167, 69);
    private final Color COLOR_WARNING_ACTION_BG = new Color(255, 193, 7);
    private final Color COLOR_DANGER_ACTION_BG = new Color(220, 53, 69);
    private final Color COLOR_TABLE_HEADER_BG = new Color(52, 58, 64);
    private final Color COLOR_TABLE_HEADER_FG = Color.WHITE;


    private final Font FONT_SIDEBAR_BUTTON = new Font("Segoe UI", Font.BOLD, 13); 
    private final Font FONT_USER_LABEL = new Font("Segoe UI", Font.BOLD, 18); 
    private final Font FONT_ACTION_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_TITLE_VIEW = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_TABLE_HEADER_CUSTOM = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TABLE_CELL_CUSTOM = new Font("Segoe UI", Font.PLAIN, 13);


    public DashboardView(String username) {
        dashboardFrame = new JFrame("Dashboard - " + username);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setSize(1024, 768); // Ukuran sedikit lebih besar
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setLayout(new BorderLayout(0, 0)); // Hapus gap jika tidak perlu
        dashboardFrame.getContentPane().setBackground(new Color(240, 240, 240));

        JPanel sidebar = createSidebar(username);
        dashboardFrame.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentArea = new JPanel(cardLayout);
        mainContentArea.setBackground(Color.WHITE);

        activityPanel = createActivityPanel();
        recycleBinPanel = createRecycleBinPanel();

        mainContentArea.add(activityPanel, "ActivityPanel");
        mainContentArea.add(recycleBinPanel, "RecycleBinPanel");

        dashboardFrame.add(mainContentArea, BorderLayout.CENTER);
        cardLayout.show(mainContentArea, "ActivityPanel");
    }

    private JPanel createSidebar(String username) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COLOR_SIDEBAR_BG_NEW);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0)); // Lebar sidebar disesuaikan
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        userLabel = new JLabel("Halo, " + username + "!");
        userLabel.setForeground(COLOR_SIDEBAR_USER_LABEL_TEXT);
        userLabel.setFont(FONT_USER_LABEL);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 20, 0));
        sidebar.add(userLabel);

        documentButton = createSidebarButtonStyled("Kelola Dokumen");
        sidebar.add(documentButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sharedDocumentsButton = createSidebarButtonStyled("Shared Documents"); // Tombol baru
        sidebar.add(sharedDocumentsButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        recycleBinButton = createSidebarButtonStyled("Recycle Bin");
        sidebar.add(recycleBinButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        refreshButton = createSidebarButtonStyled("Segarkan Aktivitas");
        sidebar.add(refreshButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        profileButton = createSidebarButtonStyled("Profil Saya"); // Tombol Profile
        sidebar.add(profileButton);
        
        sidebar.add(Box.createVerticalGlue());

        logoutButton = new JButton(" Keluar");
        styleDangerButton(logoutButton);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        sidebar.add(logoutButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        return sidebar;
    }
    
    private JButton createSidebarButtonStyled(String text) {
        JButton button = new JButton(text);
        button.setForeground(COLOR_SIDEBAR_BUTTON_TEXT_NEW);
        button.setBackground(COLOR_SIDEBAR_BUTTON_BG_NEW);
        button.setFont(FONT_SIDEBAR_BUTTON);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,180,180), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SIDEBAR_BUTTON_HOVER_BG_NEW);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SIDEBAR_BUTTON_BG_NEW);
            }
        });
        return button;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        headerPanel.setOpaque(false);
        
        cardJumlahDokumenValue = new JLabel("0");
        cardAccessValue = new JLabel("0");
        headerPanel.add(createInfoCard("Dokumen Aktif", cardJumlahDokumenValue));
        headerPanel.add(createInfoCard("Total Akses", cardAccessValue));
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            " Riwayat Aktivitas Terbaru ",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14), new Color(80,80,80)
        ));
        tablePanel.setOpaque(true);
        tablePanel.setBackground(Color.WHITE);

        String[] activityColumnNames = {"Aktivitas", "Pengguna", "Dokumen", "Tanggal", "Waktu"};
        activityTableModel = new DefaultTableModel(activityColumnNames, 0);
        activityTable = new JTable(activityTableModel);
        setupTableStyle(activityTable, true); // Menggunakan boolean untuk membedakan (opsional)
        JScrollPane scrollPaneAktivitas = new JScrollPane(activityTable);
        scrollPaneAktivitas.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneAktivitas.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPaneAktivitas, BorderLayout.CENTER);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRecycleBinPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Recycle Bin");
        titleLabel.setFont(FONT_TITLE_VIEW);
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] recycleBinColumnNames = {"Nama Dokumen", "Pemilik", "Tgl Dihapus", "Path (Internal)"};
        recycleBinTableModel = new DefaultTableModel(recycleBinColumnNames, 0);
        recycleBinTable = new JTable(recycleBinTableModel);
        setupTableStyle(recycleBinTable, true);
        JScrollPane scrollPaneRecycle = new JScrollPane(recycleBinTable);
        scrollPaneRecycle.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneRecycle.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPaneRecycle, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        restoreButton = new JButton("Restore Pilihan");
        styleSuccessButton(restoreButton);

        permanentDeleteButton = new JButton("Hapus Permanen");
        styleDangerButton(permanentDeleteButton);
        
        buttonPanel.add(restoreButton);
        buttonPanel.add(permanentDeleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private void setupTableStyle(JTable table, boolean alignLeft) { // alignLeft opsional
        table.setFont(FONT_TABLE_CELL_CUSTOM);
        table.getTableHeader().setFont(FONT_TABLE_HEADER_CUSTOM);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER_BG);
        table.getTableHeader().setForeground(COLOR_TABLE_HEADER_FG);
        table.setRowHeight(30);
        table.setGridColor(new Color(224, 224, 224));
        table.setShowVerticalLines(true); 
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new LeftAlignedHeaderRenderer(table.getTableHeader().getDefaultRenderer()));
        }
        if (table.getColumnCount() > 0 && table.getColumnName(table.getColumnCount()-1).equals("Aksi")) { 
             table.getTableHeader().getColumnModel().getColumn(table.getColumnCount()-1).setHeaderRenderer(new CenterAlignedHeaderRenderer(table.getTableHeader().getDefaultRenderer()));
        }
    }
    
    private static class LeftAlignedHeaderRenderer implements TableCellRenderer { 
        private TableCellRenderer defaultRenderer;
        public LeftAlignedHeaderRenderer(TableCellRenderer defaultRenderer) { this.defaultRenderer = defaultRenderer; }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                ((JLabel) c).setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TableHeader.cellBorder"), new EmptyBorder(0, 8, 0, 5))); // Padding kiri untuk teks header
            }
            return c;
        }
    }
    private static class CenterAlignedHeaderRenderer implements TableCellRenderer { 
        private TableCellRenderer defaultRenderer;
        public CenterAlignedHeaderRenderer(TableCellRenderer defaultRenderer) { this.defaultRenderer = defaultRenderer; }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) { ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER); }
            return c;
        }
    }


    private void stylePrimaryButton(JButton button) {
        button.setFont(FONT_ACTION_BUTTON);
        button.setBackground(COLOR_PRIMARY_ACTION_BG);
        button.setForeground(COLOR_BUTTON_TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSuccessButton(JButton button) {
        button.setFont(FONT_ACTION_BUTTON);
        button.setBackground(COLOR_SUCCESS_ACTION_BG);
        button.setForeground(COLOR_BUTTON_TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleWarningButton(JButton button) {
        button.setFont(FONT_ACTION_BUTTON);
        button.setBackground(COLOR_WARNING_ACTION_BG);
        button.setForeground(COLOR_BUTTON_TEXT_DARK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(FONT_ACTION_BUTTON);
        button.setBackground(COLOR_DANGER_ACTION_BG);
        button.setForeground(COLOR_BUTTON_TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Samakan padding dengan sidebar button jika perlu
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createInfoCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20))
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        panel.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(new Color(52, 73, 94));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }
    
    public void showPanel(String panelName) {
        if (cardLayout != null && mainContentArea != null) {
            cardLayout.show(mainContentArea, panelName);
        }
    }

    public void updateInfoCards(int documentCount, int accessCount) {
        if (cardJumlahDokumenValue != null) cardJumlahDokumenValue.setText(String.valueOf(documentCount));
        if (cardAccessValue != null) cardAccessValue.setText(String.valueOf(accessCount));
    }

    public void addActivity(String activity, String user, String document, String date, String time) {
        if (activityTableModel != null) activityTableModel.addRow(new Object[]{activity, user, document, date, time});
    }
    public void clearActivityTable() {
        if (activityTableModel != null) activityTableModel.setRowCount(0);
    }

    public void addRecycledDocumentToTable(String docName, String owner, String dateDeleted, String path) {
        if (recycleBinTableModel != null) recycleBinTableModel.addRow(new Object[]{docName, owner, dateDeleted, path});
    }
    public void clearRecycleBinTable() {
        if (recycleBinTableModel != null) recycleBinTableModel.setRowCount(0);
    }
    public int getSelectedRecycleBinTableRow() {
        return recycleBinTable != null ? recycleBinTable.getSelectedRow() : -1;
    }
    public String getDocumentNameFromRecycleBinTable(int row) {
        if (recycleBinTable != null && row >= 0 && row < recycleBinTableModel.getRowCount()) {
            return (String) recycleBinTableModel.getValueAt(row, 0);
        }
        return null;
    }

    public void updateWelcomeMessage(String username) {
        if (userLabel != null) {
            userLabel.setText("Halo, " + username + "!");
        }
    }

    public JFrame getDashboardFrame() { return dashboardFrame; }
    public JButton getLogoutButton() { return logoutButton; }
    public JButton getDocumentButton() { return documentButton; }
    public JButton getRecycleBinButton() { return recycleBinButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getProfileButton() { return profileButton; } // Getter untuk tombol Profile
    public JButton getSharedDocumentsButton() { return sharedDocumentsButton; } // Getter untuk tombol Shared Documents
    public JButton getRestoreButton() { return restoreButton; }
    public JButton getPermanentDeleteButton() { return permanentDeleteButton; }
    public DefaultTableModel getActivityTableModel() { return activityTableModel; }
    public DefaultTableModel getRecycleBinTableModel() { return recycleBinTableModel; }
}