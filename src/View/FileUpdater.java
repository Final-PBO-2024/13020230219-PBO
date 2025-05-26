package View;

import Model.AuthModel;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUpdater {
    public static boolean updateFile(JFrame parentFrame, String oldName, String oldFilePath, AuthModel model) {
        // Dialog untuk mengganti nama
        String newName = JOptionPane.showInputDialog(parentFrame, "Masukkan nama file baru:", oldName);
        if (newName == null || newName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Nama file tidak boleh kosong!");
            return false;
        }

        // Baca isi file
        File file = new File(oldFilePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(parentFrame, "File tidak ditemukan!");
            return false;
        }

        // Verifikasi bahwa file adalah .txt
        if (!oldFilePath.toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(parentFrame, "Hanya file teks (.txt) yang dapat diedit!");
            return false;
        }

        String fileContent;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            fileContent = content.toString();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Gagal membaca file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Tampilkan dialog untuk mengedit isi file
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(fileContent);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        int result = JOptionPane.showConfirmDialog(parentFrame, scrollPane, "Edit Isi File", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newContent = textArea.getText();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                writer.write(newContent);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan perubahan: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            // Update model dengan nama baru (path tetap sama)
            model.updateDocument(oldName, newName, oldFilePath);
            JOptionPane.showMessageDialog(parentFrame, "File '" + oldName + "' berhasil diperbarui menjadi '" + newName + "' dengan isi baru!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Pengeditan dibatalkan.");
            return false;
        }
    }
}