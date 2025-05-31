package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
// import java.awt.GridLayout; // Tidak terpakai di versi ini

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class DocumentView {
    private JFrame documentFrame;
    private JButton backButton; // Kembali ke Dashboard
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton accessButton;
    private JButton logoutButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton downloadButton;
    private JButton sharedDocumentsButtonSidebar; // Tombol baru di sidebar

    // Konstanta Warna dan Font
    private final Color COLOR_SIDEBAR_BG_NEW = new Color(68, 78, 96); 
    private final Color COLOR_SIDEBAR_BUTTON_BG_NEW = new Color(238, 238, 238); 
    private final Color COLOR_SIDEBAR_BUTTON_HOVER_BG_NEW = new Color(220, 220, 225); 
    private final Color COLOR_SIDEBAR_BUTTON_TEXT_NEW = new Color(50, 50, 70); 
    private final Color COLOR_SIDEBAR_TITLE_TEXT = Color.WHITE;

    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_BUTTON_TEXT_DARK = new Color(52, 58, 64); 

    private final Color COLOR_PRIMARY_ACTION_BG = new Color(0, 123, 255);
    private final Color COLOR_WARNING_ACTION_BG = new Color(255, 193, 7);
    private final Color COLOR_DANGER_ACTION_BG = new Color(220, 53, 69);

    private final Font FONT_SIDEBAR_BUTTON = new Font("Segoe UI", Font.BOLD, 13); 
    private final Font FONT_ACTION_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_TITLE_VIEW = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TABLE_CELL = new Font("Segoe UI", Font.PLAIN, 13);

    public DocumentView() {
        System.out.println("Konstruktor DocumentView dipanggil");
        documentFrame = new JFrame("Kelola Dokumen");
        documentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        documentFrame.setSize(1024, 768); // Ukuran disamakan dengan Dashboard
        documentFrame.setLocationRelativeTo(null);
        documentFrame.setLayout(new BorderLayout(0, 0));
        documentFrame.getContentPane().setBackground(new Color(240, 240, 240));

        JPanel sidebar = createSidebarPanel(); 
        documentFrame.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = createContentPanel(); 
        documentFrame.add(contentPanel, BorderLayout.CENTER);

        System.out.println("Konstruktor DocumentView selesai");
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COLOR_SIDEBAR_BG_NEW);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0)); 
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Manajemen Dokumen"); 
        titleLabel.setForeground(COLOR_SIDEBAR_TITLE_TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 0)); 
        sidebar.add(titleLabel);
        
        backButton = createSidebarButtonStyled(" Dashboard"); 
        sidebar.add(backButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); 

        sharedDocumentsButtonSidebar = createSidebarButtonStyled(" Shared Documents"); // Tombol baru
        sidebar.add(sharedDocumentsButtonSidebar);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));


        sidebar.add(Box.createVerticalGlue());

        logoutButton = new JButton(" Keluar"); 
        styleDangerButton(logoutButton); 
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        sidebar.add(logoutButton);
        sidebar.add(Box.createRigidArea(new Dimension(0,10)));

        return sidebar;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); 
        contentPanel.setBackground(Color.WHITE); 

        JLabel viewTitleLabel = new JLabel("Daftar Dokumen Anda (Aktif)"); 
        viewTitleLabel.setFont(FONT_TITLE_VIEW);
        viewTitleLabel.setForeground(new Color(50,50,50));
        viewTitleLabel.setBorder(BorderFactory.createEmptyBorder(0,0,15,0)); 
        contentPanel.add(viewTitleLabel, BorderLayout.NORTH);

        String[] columnNames = { "Nama File", "Pemilik (Email)", "Waktu Modifikasi", "Path File" }; 
        tableModel = new DefaultTableModel(columnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Membuat semua sel tidak bisa diedit langsung di tabel
            }
        };
        table = new JTable(tableModel);
        setupTableStyle(table); 
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE); 
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        buttonActionPanel.setOpaque(false); 

        addButton = new JButton("Tambah Dokumen");
        stylePrimaryButton(addButton);
        updateButton = new JButton("Update Pilihan");
        stylePrimaryButton(updateButton);
        deleteButton = new JButton("Hapus (Recycle Bin)");
        styleWarningButton(deleteButton); 
        accessButton = new JButton("Kelola Akses");
        stylePrimaryButton(accessButton);
        downloadButton = new JButton("Download Pilihan");
        stylePrimaryButton(downloadButton);

        buttonActionPanel.add(addButton);
        buttonActionPanel.add(updateButton);
        buttonActionPanel.add(deleteButton);
        buttonActionPanel.add(accessButton);
        buttonActionPanel.add(downloadButton);
        contentPanel.add(buttonActionPanel, BorderLayout.SOUTH);

        return contentPanel;
    }
    
    private void setupTableStyle(JTable table) {
        table.setFont(FONT_TABLE_CELL);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(new Color(235, 238, 241));
        table.getTableHeader().setForeground(new Color(52, 73, 94));
        table.setRowHeight(30);
        table.setGridColor(new Color(224, 224, 224));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Hanya bisa pilih 1 baris
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
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void addDocumentToTable(String name, String email, String timestamp, String filePath) {
        if (tableModel != null) {
            tableModel.addRow(new Object[] { name, email, timestamp, filePath });
        }
    }
    
    public void clearDocumentTable() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
    }

    public JFrame getDocumentFrame() { return documentFrame; }
    public JButton getBackButton() { return backButton; }
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getAccessButton() { return accessButton; }
    public JButton getLogoutButton() { return logoutButton; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() {return tableModel;}
    public JButton getDownloadButton() { return downloadButton; }
    public JButton getSharedDocumentsButtonSidebar() { return sharedDocumentsButtonSidebar; } // Getter tombol baru
}