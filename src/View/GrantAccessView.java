package View;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class GrantAccessView extends JDialog {
    private JTextField emailField;
    private JComboBox<String> accessTypeComboBox;
    private JButton addButton;
    private JButton grantButton;
    private JButton cancelButton;
    private JTable accessTable;
    private DefaultTableModel tableModel;

    public GrantAccessView(JFrame parent) {
        super(parent, "Beri Akses ke Dokumen", true); // Modal dialog
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Panel Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 30, 30));
        JLabel headerLabel = new JLabel("Beri Akses ke Dokumen", JLabel.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel Konten Utama
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Panel Input
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Komponen untuk email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);

        // Komponen untuk tipe akses
        JLabel accessLabel = new JLabel("Tipe Akses:");
        accessLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] accessTypes = {"View", "Editor"};
        accessTypeComboBox = new JComboBox<>(accessTypes);
        accessTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(accessLabel);
        inputPanel.add(accessTypeComboBox);

        // Tombol Add
        addButton = new JButton("Add");
        addButton.setBackground(new Color(0, 128, 0)); // Hijau tua
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.add(addButton);
        inputPanel.add(new JLabel()); // Placeholder
        inputPanel.add(addButtonPanel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Panel Tabel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Nama", "Email", "Role", "Aksi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Hanya kolom Aksi yang bisa diedit
            }
        };
        accessTable = new JTable(tableModel);
        accessTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        accessTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(accessTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Panel Tombol Bawah
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        grantButton = new JButton("Beri Akses");
        grantButton.setBackground(new Color(34, 139, 34)); // Hijau
        grantButton.setForeground(Color.WHITE);
        grantButton.setFont(new Font("Arial", Font.BOLD, 14));
        grantButton.setFocusPainted(false);
        buttonPanel.add(grantButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(169, 169, 169)); // Abu-abu
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Aksi default untuk tombol Cancel
        cancelButton.addActionListener(e -> dispose());
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JComboBox<String> getAccessTypeComboBox() {
        return accessTypeComboBox;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getGrantButton() {
        return grantButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JTable getAccessTable() {
        return accessTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Inner class untuk render tombol di tabel
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Hapus" : value.toString());
            return this;
        }
    }

    // Inner class untuk editor tombol di tabel
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Hapus" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = accessTable.getSelectedRow();
                if (row >= 0) {
                    String emailToRemove = (String) tableModel.getValueAt(row, 1); // Kolom Email
                    tableModel.removeRow(row);
                    // Logika untuk menghapus akses akan ditangani di controller
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}