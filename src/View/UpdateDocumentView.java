// File: View/UpdateDocumentView.java
package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Cursor; // Pastikan import Cursor ada

public class UpdateDocumentView {
    private JFrame updateFrame;
    private JTextField fileNameField;
    private JTextArea fileContentArea;
    private JButton downloadButton;
    private JButton saveButton;
    private JButton backButton;
    private JLabel originalFilePathLabel;

    // Warna dan Font (bisa disesuaikan atau diambil dari kelas utilitas/konstanta global)
    private final Color COLOR_BACKGROUND_LIGHT = new Color(245, 247, 250);
    private final Color COLOR_BUTTON_PRIMARY_BG = new Color(0, 123, 255);
    private final Color COLOR_BUTTON_SECONDARY_BG = new Color(108, 117, 125);
    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_TEXT_AREA = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    // --- KONSTRUKTOR DIPERBARUI ---
    public UpdateDocumentView(String currentFileName, String currentFilePath, String initialContent, boolean isContentEditable) {
        updateFrame = new JFrame("Update Dokumen: " + currentFileName);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setSize(750, 650);
        updateFrame.setLocationRelativeTo(null);
        updateFrame.setLayout(new BorderLayout(10, 10));
        updateFrame.getContentPane().setBackground(COLOR_BACKGROUND_LIGHT);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Nama File
        JLabel fileNameLabel = new JLabel("Nama File:");
        fileNameLabel.setFont(FONT_LABEL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fileNameLabel, gbc);

        fileNameField = new JTextField(currentFileName, 35);
        fileNameField.setFont(FONT_FIELD);
        fileNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            new EmptyBorder(5,5,5,5)
        ));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        mainPanel.add(fileNameField, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;

        // (Tombol Simpan Nama bisa ditambahkan di sini jika fungsionalitasnya terpisah)
        // saveNameButton = new JButton("Simpan Nama");
        // styleButton(saveNameButton, COLOR_PRIMARY_ACTION_BG, COLOR_BUTTON_TEXT_LIGHT);
        // gbc.gridx = 3; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        // mainPanel.add(saveNameButton, gbc);
        // gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;


        // 2. Path Asli
        JLabel filePathInfoLabel = new JLabel("Path Asli:");
        filePathInfoLabel.setFont(FONT_LABEL);
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(filePathInfoLabel, gbc);

        originalFilePathLabel = new JLabel("<html><body style='width: 300px'>" + (currentFilePath != null ? currentFilePath : "-") + "</body></html>");
        originalFilePathLabel.setFont(FONT_FIELD);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(originalFilePathLabel, gbc);
        gbc.gridwidth = 1;

        // 3. Tombol Download di samping path
        downloadButton = new JButton("Download File Asli");
        styleButton(downloadButton, new Color(108, 117, 125), COLOR_BUTTON_TEXT_LIGHT);
        gbc.gridx = 3; // Pindahkan ke kolom paling kanan jika ada saveNameButton
                       // Jika tidak ada saveNameButton di baris nama, gbc.gridx = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 1; // Sejajar dengan path asli
        gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(downloadButton, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;


        // 4. Tombol Upload Revisi (jika diperlukan)
        // uploadNewVersionButton = new JButton("Upload Revisi File");
        // styleButton(uploadNewVersionButton, new Color(40, 167, 69), COLOR_BUTTON_TEXT_LIGHT);
        // gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        // mainPanel.add(uploadNewVersionButton, gbc);
        // gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;


        // 5. Area Teks untuk Isi File (atau pesan info)
        JLabel fileContentLabel = new JLabel("Isi File:"); // Label lebih sederhana
        fileContentLabel.setFont(FONT_LABEL);
        // gbc.gridy dinaikkan jika uploadNewVersionButton diaktifkan
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; // Span semua kolom
        mainPanel.add(fileContentLabel, gbc);
        gbc.gridwidth = 1;

        fileContentArea = new JTextArea(initialContent, 15, 0); 
        fileContentArea.setFont(FONT_TEXT_AREA);
        fileContentArea.setLineWrap(true);
        fileContentArea.setWrapStyleWord(true);
        fileContentArea.setEditable(isContentEditable); 
        fileContentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180,180,180), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        if (!isContentEditable) {
            fileContentArea.setBackground(new Color(235, 235, 235));
            fileContentArea.setForeground(new Color(80, 80, 80));
        } else {
            fileContentArea.setBackground(Color.WHITE);
            fileContentArea.setForeground(Color.BLACK);
        }
        JScrollPane scrollPane = new JScrollPane(fileContentArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0; gbc.gridy = 3; // Naikkan gridy jika uploadNewVersionButton diaktifkan
        gbc.gridwidth = 4;
        gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;


        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomButtonPanel.setBackground(COLOR_BACKGROUND_LIGHT);
        bottomButtonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        backButton = new JButton("Kembali");
        styleButton(backButton, COLOR_BUTTON_SECONDARY_BG, COLOR_BUTTON_TEXT_LIGHT);
        bottomButtonPanel.add(backButton);

        saveButton = new JButton("Simpan Perubahan");
        styleButton(saveButton, COLOR_BUTTON_PRIMARY_BG, COLOR_BUTTON_TEXT_LIGHT);
        bottomButtonPanel.add(saveButton);

        updateFrame.add(mainPanel, BorderLayout.CENTER);
        updateFrame.add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color backgroundColor, Color foregroundColor) {
        button.setFont(FONT_BUTTON);
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Getters
    public JFrame getUpdateFrame() { return updateFrame; }
    public JTextField getFileNameField() { return fileNameField; }
    public JTextArea getFileContentArea() { return fileContentArea; }
    public JButton getDownloadButton() { return downloadButton; }
    public JButton getSaveButton() { return saveButton; }
    public JButton getBackButton() { return backButton; }
    public JLabel getOriginalFilePathLabel() { return originalFilePathLabel; }
    // public JButton getUploadNewVersionButton() { return uploadNewVersionButton; } // Jika diaktifkan


    // --- PASTIKAN METHOD INI ADA DAN SESUAI ---
    public void setDocumentData(String fileName, String filePath, String content, boolean isContentEditable) {
        if (updateFrame != null) {
            updateFrame.setTitle("Edit Dokumen: " + fileName);
        }
        if (fileNameField != null) {
            fileNameField.setText(fileName);
        }
        if (originalFilePathLabel != null) {
            originalFilePathLabel.setText("<html><body style='width: 400px'>" + (filePath != null ? filePath : "-") + "</body></html>");
        }
        if (fileContentArea != null) {
            fileContentArea.setText(content);
            fileContentArea.setEditable(isContentEditable);
            fileContentArea.setCaretPosition(0);
            if (!isContentEditable) {
                fileContentArea.setBackground(new Color(235, 235, 235));
                fileContentArea.setForeground(new Color(80, 80, 80));
            } else {
                fileContentArea.setBackground(Color.WHITE);
                fileContentArea.setForeground(Color.BLACK);
            }
        }
        // Anda mungkin ingin mengatur ulang status tombol save di sini berdasarkan isContentEditable
        // if (saveButton != null) {
        // saveButton.setEnabled(true); // Atau true jika nama file selalu bisa diedit
        // }
    }
    
}