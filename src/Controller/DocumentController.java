package Controller;

import Model.AuthModel;
import Model.Document;
import View.DocumentView;
import View.DashboardView;
import View.ProfileView;
import View.AuthView;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

public class DocumentController {
    private AuthModel model;
    private DocumentView view;
    private DashboardView dashboardView;
    private ProfileView profileView;
    private AuthView authView;

    public DocumentController(AuthModel model, DocumentView view, DashboardView dashboardView) {
        this.model = model;
        this.view = view;
        this.dashboardView = dashboardView;
        this.profileView = null;
        this.authView = null;
        initController();
    }

    private void initController() {
        view.getAddButton().addActionListener(ae -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(view.getDocumentFrame());

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String name = selectedFile.getName();
                String filePath = selectedFile.getAbsolutePath();
                String email = "user@example.com";
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

                System.out.println("Dipilih file: " + name + ", filePath: " + filePath);
                boolean success = model.addDocument(name, email, timestamp, filePath);
                if (success) {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "File '" + name + "' ditambahkan!");
                    updateTable();
                } else {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "Gagal menambahkan file: Nama sudah ada atau input tidak valid!");
                }
            }
        });

        view.getUpdateButton().addActionListener(ue -> {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                String oldName = (String) view.getTable().getValueAt(row, 0);

                // Minta nama baru
                String newName = JOptionPane.showInputDialog(view.getDocumentFrame(), "Masukkan nama baru:", oldName);
                if (newName == null || newName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "Nama baru tidak boleh kosong!");
                    return;
                }

                // Minta email baru
                String email = JOptionPane.showInputDialog(view.getDocumentFrame(), "Masukkan email baru:", "user@example.com");
                if (email == null || email.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "Email tidak boleh kosong!");
                    return;
                }

                // Baca isi file lama
                String oldFilePath = model.getDocuments().get(oldName).getFilePath();
                String content = "";
                try {
                    content = new String(Files.readAllBytes(Paths.get(oldFilePath)));
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "Gagal membaca file: " + e.getMessage());
                    return;
                }

                // Buat dialog untuk mengedit isi file
                JTextArea textArea = new JTextArea(content, 10, 40);
                JScrollPane scrollPane = new JScrollPane(textArea);
                int option = JOptionPane.showConfirmDialog(view.getDocumentFrame(), scrollPane, "Edit Isi File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }

                String newContent = textArea.getText();
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

                // Perbarui dokumen dengan isi baru
                if (model.updateDocument(oldName, newName, email, timestamp, newContent)) {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "File '" + oldName + "' diperbarui menjadi '" + newName + "'!");
                    updateTable();
                } else {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "Gagal memperbarui file: Nama baru mungkin sudah ada atau input tidak valid!");
                }
            } else {
                JOptionPane.showMessageDialog(view.getDocumentFrame(), "Pilih file yang ingin diperbarui!");
            }
        });

        view.getDeleteButton().addActionListener(de -> {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                String name = (String) view.getTable().getValueAt(row, 0);
                model.removeDocument(name);
                JOptionPane.showMessageDialog(view.getDocumentFrame(), "File '" + name + "' dihapus!");
                updateTable();
            } else {
                JOptionPane.showMessageDialog(view.getDocumentFrame(), "Pilih file yang ingin dihapus!");
            }
        });

        view.getAccessButton().addActionListener(ae -> {
            JOptionPane.showMessageDialog(view.getDocumentFrame(), "Fitur Beri Akses belum diimplementasikan.");
        });

        // Aksi untuk tombol Kembali ke Dashboard
        view.getBackButton().addActionListener(e -> {
            if (dashboardView.getDashboardFrame() != null) {
                dashboardView.getDashboardFrame().setVisible(true);
                view.getDocumentFrame().setVisible(false);
            } else {
                JOptionPane.showMessageDialog(view.getDocumentFrame(), "Dashboard tidak tersedia!");
            }
        });

        // Aksi untuk tombol Profile
        view.getProfileButton().addActionListener(e -> {
            if (profileView == null) {
                profileView = new ProfileView("Document");
                if (profileView.getProfileFrame() == null) {
                    JOptionPane.showMessageDialog(view.getDocumentFrame(), "Profile tidak dapat dimuat!");
                    return;
                }
                new ProfileController(model, profileView, dashboardView);
                profileView.getBackButton().addActionListener(backEvent -> {
                    profileView.getProfileFrame().setVisible(false);
                    view.getDocumentFrame().setVisible(true);
                });
            }
            if (profileView.getProfileFrame() != null) {
                profileView.getProfileFrame().setVisible(true);
                view.getDocumentFrame().setVisible(false);
            } else {
                JOptionPane.showMessageDialog(view.getDocumentFrame(), "Profile tidak tersedia!");
            }
        });

        // Aksi untuk tombol Keluar (Logout)
        view.getLogoutButton().addActionListener(e -> {
            if (authView != null) {
                view.getDocumentFrame().setVisible(false);
                authView.getLoginFrame().setVisible(true);
                authView.setLoginStatus("", Color.BLACK);
            } else {
                JOptionPane.showMessageDialog(view.getDocumentFrame(), "Tidak dapat logout: AuthView tidak tersedia!");
            }
        });

        // Aksi sementara untuk tombol lain
        view.getFriendsButton().addActionListener(e -> System.out.println("Tombol List Teman diklik"));
        view.getLoginHistoryButton().addActionListener(e -> System.out.println("Tombol Riwayat Login diklik"));
        view.getUserButton().addActionListener(e -> System.out.println("Tombol User diklik"));
        view.getSettingsButton().addActionListener(e -> System.out.println("Tombol Pengaturan diklik"));
    }

    private void updateTable() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTable().getModel();
        tableModel.setRowCount(0);
        System.out.println("Memperbarui tabel. Jumlah dokumen: " + model.getDocuments().size());
        for (Document doc : model.getDocuments().values()) {
            System.out.println("Menambahkan ke tabel: " + doc.getName());
            tableModel.addRow(new Object[]{doc.getName(), doc.getEmail(), doc.getTimestamp()});
        }
    }

    public DocumentView getDocumentView() {
        return view;
    }

    public void setAuthView(AuthView authView) {
        this.authView = authView;
    }
}