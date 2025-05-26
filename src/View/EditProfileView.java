package View;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class EditProfileView {
    private JDialog editDialog;
    private JTextField usernameField;
    private JTextField emailField;
    private JButton uploadPhotoButton;
    private JLabel photoPathLabel;
    private JButton saveButton;
    private JButton cancelButton;

    public EditProfileView(JFrame parent, String currentUsername, String currentEmail, String currentPhotoPath) {
        editDialog = new JDialog(parent, "Ubah Profile", true);
        editDialog.setSize(300, 250);
        editDialog.setLocationRelativeTo(parent);
        editDialog.setLayout(new GridLayout(5, 2, 10, 10));

        // Komponen untuk username
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(currentUsername.replace("Username: ", ""));
        editDialog.add(usernameLabel);
        editDialog.add(usernameField);

        // Komponen untuk email
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(currentEmail.replace("Email: ", ""));
        editDialog.add(emailLabel);
        editDialog.add(emailField);

        // Komponen untuk foto
        JLabel photoLabel = new JLabel("Foto Profil:");
        uploadPhotoButton = new JButton("Pilih Foto");
        photoPathLabel = new JLabel(currentPhotoPath.equals("Foto: belum diunggah") ? "Belum dipilih" : currentPhotoPath.replace("Foto: ", ""));
        uploadPhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(editDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                photoPathLabel.setText(selectedFile.getAbsolutePath());
            }
        });
        editDialog.add(photoLabel);
        editDialog.add(uploadPhotoButton);
        editDialog.add(new JLabel()); // Spacer
        editDialog.add(photoPathLabel);

        // Tombol Simpan dan Batal
        saveButton = new JButton("Simpan");
        cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> editDialog.dispose());
        editDialog.add(saveButton);
        editDialog.add(cancelButton);
    }

    public JDialog getEditDialog() {
        return editDialog;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JLabel getPhotoPathLabel() {
        return photoPathLabel;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}