// File: View/SharedDocumentView.java
package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener; // Untuk inner class editor tombol

public class SharedDocumentView {
    private JFrame sharedFrame;
    private JTable documentsYouSharedTable;
    private DefaultTableModel documentsYouSharedTableModel;
    private JTable documentsSharedWithYouTable;
    private DefaultTableModel documentsSharedWithYouTableModel;

    // Tombol Sidebar
    private JButton backToDashboardButton;
    private JButton logoutButton;

    // Warna dan Font
    private final Color COLOR_BACKGROUND_VIEW = new Color(248, 249, 250); // Latar lebih terang
    private final Color COLOR_TABLE_HEADER_BG = new Color(52, 58, 64);
    private final Color COLOR_TABLE_HEADER_FG = Color.WHITE;
    private final Color COLOR_BUTTON_VIEW_BG = new Color(23, 162, 184); // Biru info
    private final Color COLOR_BUTTON_DOWNLOAD_BG = new Color(40, 167, 69); // Hijau sukses
    private final Color COLOR_BUTTON_UPDATE_BG = new Color(255, 193, 7); // Kuning warning
    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_BUTTON_TEXT_DARK = new Color(33,37,41); // Untuk tombol kuning
    private final Color COLOR_EMPTY_TABLE_MESSAGE_BG = new Color(227, 242, 253); // Biru sangat muda
    private final Color COLOR_EMPTY_TABLE_MESSAGE_FG = new Color(23, 107, 170);

    private final Font FONT_MAIN_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font FONT_SECTION_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_TABLE_HEADER_CUSTOM = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_TABLE_CELL_CUSTOM = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BUTTON_ACTION_TABLE = new Font("Segoe UI", Font.BOLD, 11);

    // Konstanta warna sidebar (konsisten dengan view lain)
    private final Color COLOR_SIDEBAR_BG = new Color(68, 78, 96);
    private final Color COLOR_SIDEBAR_BUTTON_BG = new Color(238, 238, 238);
    private final Color COLOR_SIDEBAR_BUTTON_HOVER_BG = new Color(220, 220, 225);
    private final Color COLOR_SIDEBAR_BUTTON_TEXT = new Color(50, 50, 70);
    private final Color COLOR_SIDEBAR_TITLE_TEXT = Color.WHITE;
    private final Color COLOR_DANGER_ACTION_BG = new Color(220, 53, 69);
    private final Font FONT_SIDEBAR_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_ACTION_BUTTON = new Font("Segoe UI", Font.BOLD, 13); // Untuk tombol danger

    // Action commands untuk tombol di tabel
    public static final String ACTION_CMD_VIEW_PROFILE = "VIEW_PROFILE_SHARED";
    public static final String ACTION_CMD_DELETE_ACCESS = "DELETE_ACCESS_SHARED";
    public static final String ACTION_CMD_VIEW_DOC_SHARED_TO_YOU = "VIEW_DOC_SHARED_TO_YOU";
    public static final String ACTION_CMD_DOWNLOAD_DOC_SHARED_TO_YOU = "DOWNLOAD_DOC_SHARED_TO_YOU";
    public static final String ACTION_CMD_UPDATE_DOC_SHARED_TO_YOU = "UPDATE_DOC_SHARED_TO_YOU";


    public SharedDocumentView(String currentUsername) {
        sharedFrame = new JFrame("Dokumen Dibagikan - " + currentUsername);
        sharedFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sharedFrame.setSize(1024, 768);
        sharedFrame.setLocationRelativeTo(null);
        sharedFrame.setLayout(new BorderLayout(0, 0));
        sharedFrame.getContentPane().setBackground(COLOR_BACKGROUND_VIEW);

        JPanel sidebar = createSidebarPanel(currentUsername);
        sharedFrame.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BACKGROUND_VIEW);
        contentPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel mainTitleLabel = new JLabel("Shared Documents");
        mainTitleLabel.setFont(FONT_MAIN_TITLE);
        mainTitleLabel.setForeground(new Color(55, 55, 55));
        mainTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainTitleLabel.setBorder(new EmptyBorder(0, 5, 25, 0)); // Padding kiri dan bawah
        contentPanel.add(mainTitleLabel);

        contentPanel.add(createSectionPanel("Documents You Shared", true));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        contentPanel.add(createSectionPanel("Documents Shared with You", false));
        
        JScrollPane scrollableContent = new JScrollPane(contentPanel);
        scrollableContent.setBorder(null);
        scrollableContent.getVerticalScrollBar().setUnitIncrement(16);
        scrollableContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        sharedFrame.add(scrollableContent, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel(String username) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COLOR_SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setLayout(new BoxLayout(sidebarHeader, BoxLayout.X_AXIS));
        sidebarHeader.setBackground(COLOR_SIDEBAR_BG);
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        sidebarHeader.setBorder(new EmptyBorder(10, 20, 20, 15));

        JLabel sidebarTitle = new JLabel("Shared Area"); // Atau nama pengguna
        sidebarTitle.setForeground(COLOR_SIDEBAR_TITLE_TEXT);
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarHeader.add(sidebarTitle);
        sidebar.add(sidebarHeader);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        backToDashboardButton = createSidebarButtonStyled(" Dashboard");
        sidebar.add(backToDashboardButton);
        
        sidebar.add(Box.createVerticalGlue());

        logoutButton = new JButton(" Keluar");
        styleDangerButton(logoutButton);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        sidebar.add(logoutButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        return sidebar;
    }

    private JPanel createSectionPanel(String title, boolean isDocumentsYouShared) {
        JPanel sectionPanel = new JPanel(new BorderLayout(0, 10));
        sectionPanel.setOpaque(false);

        JLabel sectionTitleLabel = new JLabel(title);
        sectionTitleLabel.setFont(FONT_SECTION_TITLE);
        sectionTitleLabel.setForeground(new Color(60, 60, 60));
        sectionTitleLabel.setBorder(new EmptyBorder(0, 5, 10, 0)); // Padding kiri sedikit
        sectionPanel.add(sectionTitleLabel, BorderLayout.NORTH);

        if (isDocumentsYouShared) {
            String[] columnsYouShared = {"Nama File", "Dibagikan Ke", "Tanggal Akses", "Aksi"};
            documentsYouSharedTableModel = new DefaultTableModel(columnsYouShared, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return column == 3; }
            };
            documentsYouSharedTable = new JTable(documentsYouSharedTableModel);
            setupTableStyle(documentsYouSharedTable);
            TableColumn actionsColumn = documentsYouSharedTable.getColumnModel().getColumn(3);
            actionsColumn.setCellRenderer(new ButtonsPanelRenderer());
            actionsColumn.setCellEditor(new ButtonsPanelEditor(documentsYouSharedTable, true));
            actionsColumn.setMinWidth(180); actionsColumn.setMaxWidth(220); actionsColumn.setPreferredWidth(200);

            sectionPanel.add(new JScrollPane(documentsYouSharedTable), BorderLayout.CENTER);
        } else {
            String[] columnsSharedWithYou = {"Nama File", "Dibagikan Oleh", "Aksi Diberikan"}; // Kolom aksi menampilkan teks permission
            documentsSharedWithYouTableModel = new DefaultTableModel(columnsSharedWithYou, 0) {
                 @Override
                public boolean isCellEditable(int row, int column) { return column == 2; } // Kolom Aksi
            };
            documentsSharedWithYouTable = new JTable(documentsSharedWithYouTableModel);
            setupTableStyle(documentsSharedWithYouTable);
            TableColumn actionsColumn = documentsSharedWithYouTable.getColumnModel().getColumn(2);
            actionsColumn.setCellRenderer(new ButtonsPanelRenderer());
            actionsColumn.setCellEditor(new ButtonsPanelEditor(documentsSharedWithYouTable, false));
            actionsColumn.setMinWidth(220); actionsColumn.setMaxWidth(280); actionsColumn.setPreferredWidth(250);


            sectionPanel.add(new JScrollPane(documentsSharedWithYouTable), BorderLayout.CENTER);
        }
        return sectionPanel;
    }
    
    private void setupTableStyle(JTable table) {
        table.setFont(FONT_TABLE_CELL_CUSTOM);
        table.getTableHeader().setFont(FONT_TABLE_HEADER_CUSTOM);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER_BG);
        table.getTableHeader().setForeground(COLOR_TABLE_HEADER_FG);
        table.setRowHeight(40); // Tinggi baris disesuaikan untuk tombol
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(220,220,220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableCellRenderer baseRenderer = table.getTableHeader().getDefaultRenderer();
            if (i == table.getColumnCount() -1 ) { // Kolom aksi rata tengah
                 table.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new CenterAlignedHeaderRenderer(baseRenderer));
            } else {
                 table.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new LeftAlignedHeaderRenderer(baseRenderer));
            }
        }
    }

    private JButton createSidebarButtonStyled(String text) {
        JButton button = new JButton(text);
        button.setForeground(COLOR_SIDEBAR_BUTTON_TEXT);
        button.setBackground(COLOR_SIDEBAR_BUTTON_BG);
        button.setFont(FONT_SIDEBAR_BUTTON);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190,190,190), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SIDEBAR_BUTTON_HOVER_BG);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SIDEBAR_BUTTON_BG);
            }
        });
        return button;
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

    private static class LeftAlignedHeaderRenderer implements TableCellRenderer { 
        private TableCellRenderer defaultRenderer;
        public LeftAlignedHeaderRenderer(TableCellRenderer defaultRenderer) { this.defaultRenderer = defaultRenderer; }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                ((JLabel) c).setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TableHeader.cellBorder"), new EmptyBorder(0, 8, 0, 5)));
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

    // Getters
    public JFrame getSharedFrame() { return sharedFrame; }
    public JButton getBackToDashboardButton() { return backToDashboardButton; }
    public JButton getLogoutButton() { return logoutButton; }
    public DefaultTableModel getDocumentsYouSharedTableModel() { return documentsYouSharedTableModel; }
    public JTable getDocumentsYouSharedTable() { return documentsYouSharedTable; }
    public DefaultTableModel getDocumentsSharedWithYouTableModel() { return documentsSharedWithYouTableModel; }
    public JTable getDocumentsSharedWithYouTable() { return documentsSharedWithYouTable; }

    public void setVisible(boolean visible) {
        sharedFrame.setVisible(visible);
        if (visible) {
            sharedFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    public void showEmptyYouSharedMessage(boolean show) {
        DefaultTableModel model = getDocumentsYouSharedTableModel();
        model.setRowCount(0); // Hapus data lama dulu
        if (show) {
            model.addRow(new Object[]{"Anda belum membagikan dokumen apapun.", "", "", ""});
            // Anda mungkin ingin menonaktifkan seleksi untuk baris ini atau memberinya tampilan khusus
        }
    }
    public void showEmptySharedWithYouMessage(boolean show) {
        DefaultTableModel model = getDocumentsSharedWithYouTableModel();
        model.setRowCount(0);
        if (show) {
            model.addRow(new Object[]{"Tidak ada dokumen yang dibagikan kepada Anda.", "", ""});
        }
    }
}

// --- INNER CLASSES UNTUK TOMBOL DI DALAM TABEL ---
class ButtonsPanelRenderer extends JPanel implements TableCellRenderer {
    // Tombol-tombol akan dibuat dan ditambahkan oleh ButtonsPanelEditor atau di sini berdasarkan nilai sel
    public ButtonsPanelRenderer() {
        super(new FlowLayout(FlowLayout.CENTER, 3, 2)); // Kurangi spasi horizontal antar tombol
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        removeAll(); // Hapus tombol lama

        // Value seharusnya berisi panel tombol yang sudah disiapkan oleh editor atau controller
        if (value instanceof JPanel) {
            // Ini agak rumit, karena renderer idealnya hanya menampilkan.
            // Biasanya, kita membuat tombol-tombol di sini berdasarkan data atau string permission.
            // Untuk sekarang, kita asumsikan value adalah String yang berisi tipe panel atau panel itu sendiri
            // Jika ButtonsPanelEditor mengembalikan JPanel, maka kita bisa langsung add.
            // Namun, value di model tabel biasanya bukan JPanel.
            // Mari kita buat tombol di sini berdasarkan boolean isForYouSharedTable (yang perlu di-pass)
            // atau berdasarkan string permission.
            // Untuk sekarang, biarkan kosong, ButtonsPanelEditor yang akan menangani tampilan saat editing.
            // Jika tidak ada editor, renderer harus menggambar tombol.

            // Versi sederhana: jika value adalah JPanel yang sudah berisi tombol
            // if (value instanceof JPanel) {
            //     this.add((JPanel)value);
            //     return this;
            // }

            // Placeholder, tombol akan dibuat oleh Editor atau Controller saat mengisi tabel
            // JLabel placeholder = new JLabel("Aksi...");
            // placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            // add(placeholder);

        } else if (value instanceof String) { // Jika value adalah string permission untuk SharedWithYou
            String permissions = (String) value;
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 10);
            Dimension btnDim = new Dimension(75, 25);
            Insets btnMargin = new Insets(2,2,2,2);

            if (permissions.contains("view")) {
                JButton viewBtn = new JButton("View");
                viewBtn.setFont(buttonFont); viewBtn.setMargin(btnMargin); viewBtn.setPreferredSize(btnDim);
                viewBtn.setBackground(new Color(23, 162, 184)); viewBtn.setForeground(Color.WHITE);
                viewBtn.setOpaque(true); viewBtn.setBorderPainted(false);
                add(viewBtn);
            }
            if (permissions.contains("download")) {
                JButton downloadBtn = new JButton("Download");
                downloadBtn.setFont(buttonFont); downloadBtn.setMargin(btnMargin); downloadBtn.setPreferredSize(btnDim);
                downloadBtn.setBackground(new Color(40, 167, 69)); downloadBtn.setForeground(Color.WHITE);
                downloadBtn.setOpaque(true); downloadBtn.setBorderPainted(false);
                add(downloadBtn);
            }
            if (permissions.contains("update")) {
                JButton updateBtn = new JButton("Update");
                 updateBtn.setFont(buttonFont); updateBtn.setMargin(btnMargin); updateBtn.setPreferredSize(btnDim);
                updateBtn.setBackground(new Color(255, 193, 7)); updateBtn.setForeground(new Color(50,50,50));
                updateBtn.setOpaque(true); updateBtn.setBorderPainted(false);
                add(updateBtn);
            }
        } else if (value == null && column == table.getColumnModel().getColumnIndex("Aksi")) { // Untuk tabel "You Shared"
             Font buttonFont = new Font("Segoe UI", Font.BOLD, 10);
             Dimension btnDim = new Dimension(90, 25); // Sedikit lebih lebar
             Insets btnMargin = new Insets(2,2,2,2);

             JButton viewProfileBtn = new JButton("View Profile");
             viewProfileBtn.setFont(buttonFont); viewProfileBtn.setMargin(btnMargin); viewProfileBtn.setPreferredSize(btnDim);
             viewProfileBtn.setBackground(new Color(108, 117, 125)); viewProfileBtn.setForeground(Color.WHITE);
             viewProfileBtn.setOpaque(true); viewProfileBtn.setBorderPainted(false);
             add(viewProfileBtn);

             JButton deleteAccessBtn = new JButton("Del Access");
             deleteAccessBtn.setFont(buttonFont); deleteAccessBtn.setMargin(btnMargin); deleteAccessBtn.setPreferredSize(btnDim);
             deleteAccessBtn.setBackground(new Color(220, 53, 69)); deleteAccessBtn.setForeground(Color.WHITE);
             deleteAccessBtn.setOpaque(true); deleteAccessBtn.setBorderPainted(false);
             add(deleteAccessBtn);
        }


        return this;
    }
}

class ButtonsPanelEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JTable currentTable;
    private int currentRow;
    private boolean isForYouSharedTable; // Untuk membedakan tombol yang ditampilkan

    // Tombol untuk "Documents You Shared"
    private JButton viewProfileButton;
    private JButton deleteAccessButton;

    // Tombol untuk "Documents Shared With You"
    private JButton viewDocButton;
    private JButton downloadDocButton;
    private JButton updateDocButton;

    private String currentPermissionsOrAction; // Menyimpan data dari sel (misalnya, string permission)

    public ButtonsPanelEditor(JTable table, boolean forYouSharedTable) {
        this.isForYouSharedTable = forYouSharedTable;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
        panel.setOpaque(true);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 10);
        Insets btnMargin = new Insets(2,2,2,2);
        
        if (isForYouSharedTable) {
            Dimension btnDimYouShared = new Dimension(90, 25);
            viewProfileButton = new JButton("View Profile");
            viewProfileButton.setFont(buttonFont); viewProfileButton.setMargin(btnMargin); viewProfileButton.setPreferredSize(btnDimYouShared);
            viewProfileButton.setBackground(new Color(108, 117, 125)); viewProfileButton.setForeground(Color.WHITE);
            viewProfileButton.setOpaque(true); viewProfileButton.setBorderPainted(false);
            viewProfileButton.setActionCommand(SharedDocumentView.ACTION_CMD_VIEW_PROFILE);
            viewProfileButton.addActionListener(e -> fireEditingAction(e.getActionCommand()));
            panel.add(viewProfileButton);

            deleteAccessButton = new JButton("Del Access");
            deleteAccessButton.setFont(buttonFont); deleteAccessButton.setMargin(btnMargin); deleteAccessButton.setPreferredSize(btnDimYouShared);
            deleteAccessButton.setBackground(new Color(220, 53, 69)); deleteAccessButton.setForeground(Color.WHITE);
            deleteAccessButton.setOpaque(true); deleteAccessButton.setBorderPainted(false);
            deleteAccessButton.setActionCommand(SharedDocumentView.ACTION_CMD_DELETE_ACCESS);
            deleteAccessButton.addActionListener(e -> fireEditingAction(e.getActionCommand()));
            panel.add(deleteAccessButton);
        } else {
            Dimension btnDimSharedWithYou = new Dimension(75, 25);
            viewDocButton = new JButton("View");
            viewDocButton.setFont(buttonFont); viewDocButton.setMargin(btnMargin); viewDocButton.setPreferredSize(btnDimSharedWithYou);
            viewDocButton.setBackground(new Color(23, 162, 184)); viewDocButton.setForeground(Color.WHITE);
            viewDocButton.setOpaque(true); viewDocButton.setBorderPainted(false);
            viewDocButton.setActionCommand(SharedDocumentView.ACTION_CMD_VIEW_DOC_SHARED_TO_YOU);
            viewDocButton.addActionListener(e -> fireEditingAction(e.getActionCommand()));
            // Tombol ditambahkan ke panel di getTableCellEditorComponent berdasarkan permission

            downloadDocButton = new JButton("Download");
            downloadDocButton.setFont(buttonFont); downloadDocButton.setMargin(btnMargin); downloadDocButton.setPreferredSize(btnDimSharedWithYou);
            downloadDocButton.setBackground(new Color(40, 167, 69)); downloadDocButton.setForeground(Color.WHITE);
            downloadDocButton.setOpaque(true); downloadDocButton.setBorderPainted(false);
            downloadDocButton.setActionCommand(SharedDocumentView.ACTION_CMD_DOWNLOAD_DOC_SHARED_TO_YOU);
            downloadDocButton.addActionListener(e -> fireEditingAction(e.getActionCommand()));

            updateDocButton = new JButton("Update");
            updateDocButton.setFont(buttonFont); updateDocButton.setMargin(btnMargin); updateDocButton.setPreferredSize(btnDimSharedWithYou);
            updateDocButton.setBackground(new Color(255, 193, 7)); updateDocButton.setForeground(new Color(50,50,50));
            updateDocButton.setOpaque(true); updateDocButton.setBorderPainted(false);
            updateDocButton.setActionCommand(SharedDocumentView.ACTION_CMD_UPDATE_DOC_SHARED_TO_YOU);
            updateDocButton.addActionListener(e -> fireEditingAction(e.getActionCommand()));
        }
    }
    
    private void fireEditingAction(String actionCommand) {
        // Simpan action command agar bisa diambil oleh controller melalui getCellEditorValue()
        // atau controller bisa langsung merespons di sini jika ada referensi
        // Untuk sekarang, kita akan menghentikan editing saja. Controller akan cek baris dan kolom.
        // Lebih baik: gunakan PropertyChangeSupport untuk memberi tahu controller tentang aksi.
        currentTable.putClientProperty("LAST_ACTION_COMMAND_SHARED_DOCS", actionCommand); // Simpan di client property tabel
        fireEditingStopped();
        // Controller akan memproses aksi di listener mouse tabel, mengambil info dari client property jika perlu
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentTable = table;
        this.currentRow = row;
        this.currentPermissionsOrAction = value != null ? value.toString() : "";

        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }
        
        panel.removeAll(); // Hapus tombol lama sebelum menambahkan yang baru
        if (isForYouSharedTable) {
            panel.add(viewProfileButton);
            panel.add(deleteAccessButton);
        } else { // Untuk tabel "Shared With You"
            if (currentPermissionsOrAction.contains("view")) panel.add(viewDocButton);
            if (currentPermissionsOrAction.contains("download")) panel.add(downloadDocButton);
            if (currentPermissionsOrAction.contains("update")) panel.add(updateDocButton);
        }
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        // Kembalikan nilai yang relevan jika sel diedit.
        // Karena kita menggunakan tombol, kita bisa mengembalikan string permission atau null.
        return currentPermissionsOrAction;
    }
}