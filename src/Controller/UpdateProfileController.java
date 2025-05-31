// File: Controller/UpdateProfileController.java
package Controller;

import Model.AuthModel;
import View.ProfileView;
import View.UpdateProfileView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
// import java.text.SimpleDateFormat; // Tidak digunakan jika hanya update profil
// import java.util.Date;           // Tidak digunakan jika hanya update profil
// import java.awt.Color; // Tidak secara langsung digunakan di sini

public class UpdateProfileController {
    private UpdateProfileView view;
    private AuthModel model;
    private ProfileView parentProfileView; // Untuk navigasi kembali ke ProfileView utama
    private ProfileController parentProfileController; // Untuk memanggil refresh data di ProfileView
    private String currentUserEmail;
    private String currentProfilePicturePathOnLoad; // Path foto saat view pertama kali dimuat/dibuka
    private String newSelectedProfilePicturePath; // Path foto baru yang dipilih pengguna (jika ada)

    // Konstruktor yang dimodifikasi untuk menerima ProfileController
    public UpdateProfileController(UpdateProfileView upView, AuthModel authModel, ProfileView pView,
            ProfileController pController, String userEmail, String profilePicPath) {
        this.view = upView;
        this.model = authModel;
        this.parentProfileView = pView;
        this.parentProfileController = pController; // Simpan referensi ke ProfileController
        this.currentUserEmail = userEmail;
        this.currentProfilePicturePathOnLoad = profilePicPath;
        this.newSelectedProfilePicturePath = null;

        initListeners();
        loadInitialDataToView();
    }

    // Dipanggil jika instance controller ini digunakan kembali
    public void setCurrentUserData(String email, String profilePicPath) {
        this.currentUserEmail = email;
        this.currentProfilePicturePathOnLoad = profilePicPath;
        this.newSelectedProfilePicturePath = null;
        loadInitialDataToView();
    }

    private void loadInitialDataToView() {
        String username = model.getUsernameByEmail(currentUserEmail);
        view.setUsername(username != null ? username : currentUserEmail.split("@")[0]);
        view.setEmail(currentUserEmail);
        view.loadProfilePicture(currentProfilePicturePathOnLoad);
        view.getNewPasswordField().setText("");
        view.getConfirmNewPasswordField().setText("");
    }

    private void initListeners() {
        view.getCancelButton().addActionListener(e -> {
            view.getUpdateProfileFrame().dispose();
            if (parentProfileView != null) {
                parentProfileView.setVisible(true); // Ini akan memaksimalkan frame parent juga
            }
        });

        view.getChangePictureButton().addActionListener(e -> handleChangePicture());
        view.getDeletePictureButton().addActionListener(e -> handleDeletePicture());
        view.getSaveChangesButton().addActionListener(e -> handleSaveChanges());
    }

    private void handleChangePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Foto Profil Baru");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
        int returnValue = fileChooser.showOpenDialog(view.getUpdateProfileFrame());

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            newSelectedProfilePicturePath = selectedFile.getAbsolutePath();
            view.loadProfilePicture(newSelectedProfilePicturePath);
            // Tidak perlu JOptionPane di sini, biarkan user save untuk konfirmasi akhir
        }
    }

    private void handleDeletePicture() {
        int confirm = JOptionPane.showConfirmDialog(view.getUpdateProfileFrame(),
                "Apakah Anda yakin ingin menghapus foto profil saat ini?\nPerubahan akan disimpan saat Anda menekan 'Ubah Sekarang'.",
                "Konfirmasi Hapus Foto",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            newSelectedProfilePicturePath = ""; // Tandai bahwa foto dihapus (akan disimpan sebagai null/kosong di
                                                // model)
            view.loadProfilePicture(null); // Hapus tampilan foto (tampilkan placeholder)
        }
    }

    private void handleSaveChanges() {
        String newUsername = view.getUsernameField().getText().trim();
        String newPassword = new String(view.getNewPasswordField().getPassword());
        String confirmNewPassword = new String(view.getConfirmNewPasswordField().getPassword());

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(view.getUpdateProfileFrame(), "Username tidak boleh kosong.",
                    "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            view.getUsernameField().requestFocus();
            return;
        }

        boolean passwordToBeChanged = newPassword.length() > 0;
        if (passwordToBeChanged) {
            if (newPassword.length() < 6) { // Contoh validasi panjang minimal
                JOptionPane.showMessageDialog(view.getUpdateProfileFrame(), "Password baru minimal 6 karakter.",
                        "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                view.getNewPasswordField().requestFocus();
                return;
            }
            if (!newPassword.equals(confirmNewPassword)) {
                JOptionPane.showMessageDialog(view.getUpdateProfileFrame(),
                        "Password baru dan konfirmasi password tidak cocok.", "Validasi Gagal",
                        JOptionPane.WARNING_MESSAGE);
                view.getNewPasswordField().setText("");
                view.getConfirmNewPasswordField().setText("");
                view.getNewPasswordField().requestFocus();
                return;
            }
        }

        String finalProfilePicturePathToSave = currentProfilePicturePathOnLoad; // Defaultnya path lama

        if (newSelectedProfilePicturePath != null) { // Jika ada interaksi dengan tombol ubah/hapus foto
            if (newSelectedProfilePicturePath.isEmpty()) { // Pengguna memilih untuk menghapus foto
                                                           // (newSelectedProfilePicturePath di-set ke "")
                finalProfilePicturePathToSave = null; // Simpan null atau path ke gambar default Anda
                System.out.println("DEBUG: Foto profil akan dihapus (path di-set ke null).");
                // Logika untuk menghapus file lama dari server/direktori jika perlu
                // if (currentProfilePicturePathOnLoad != null &&
                // !currentProfilePicturePathOnLoad.startsWith("placeholder/") &&
                // !currentProfilePicturePathOnLoad.isEmpty()) {
                // try {
                // Files.deleteIfExists(Paths.get(currentProfilePicturePathOnLoad));
                // System.out.println("DEBUG: File foto profil lama dihapus: " +
                // currentProfilePicturePathOnLoad);
                // } catch (IOException ex) {
                // System.err.println("DEBUG: Gagal menghapus file foto profil lama: " +
                // ex.getMessage());
                // }
                // }
            } else { // Pengguna memilih foto baru
                File newPicFile = new File(newSelectedProfilePicturePath);
                if (!newPicFile.exists() || !newPicFile.isFile()) {
                    JOptionPane.showMessageDialog(view.getUpdateProfileFrame(),
                            "File foto profil baru yang dipilih tidak valid.", "Error File", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String targetDir = "user_images/"; // Direktori penyimpanan foto pengguna (relatif terhadap root proyek)
                File targetDirFile = new File(targetDir);
                if (!targetDirFile.exists()) {
                    if (!targetDirFile.mkdirs()) {
                        JOptionPane.showMessageDialog(view.getUpdateProfileFrame(),
                                "Gagal membuat direktori penyimpanan foto.", "Error File", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                // Buat nama file unik untuk menghindari konflik
                String fileExtension = "";
                String originalName = newPicFile.getName();
                int lastDot = originalName.lastIndexOf('.');
                if (lastDot > 0 && lastDot < originalName.length() - 1) {
                    fileExtension = originalName.substring(lastDot);
                }
                String uniqueFileName = (currentUserEmail != null ? currentUserEmail.split("@")[0] : "user")
                        + "_" + System.currentTimeMillis() + fileExtension;
                Path targetPath = Paths.get(targetDir, uniqueFileName);
                try {
                    Files.copy(newPicFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    finalProfilePicturePathToSave = targetPath.toString().replace("\\", "/");
                    System.out.println("Foto profil baru disalin ke: " + finalProfilePicturePathToSave);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view.getUpdateProfileFrame(),
                            "Gagal menyimpan file foto profil baru: " + ex.getMessage(), "Error File",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return; // Batalkan proses simpan jika gagal menyalin foto baru
                }
            }
        }

        System.out.println("DEBUG handleSaveChanges: currentUserEmail=" + currentUserEmail +
                ", newUsername=" + newUsername +
                ", passwordChanged=" + passwordToBeChanged +
                ", finalProfilePicturePathToSave=" + finalProfilePicturePathToSave);

        // Panggil method di AuthModel untuk update data pengguna
        boolean success = model.updateUserProfile(
                currentUserEmail,
                newUsername,
                passwordToBeChanged ? newPassword : null, 
                finalProfilePicturePathToSave // Ini adalah path yang akan disimpan ke model
        );

        System.out.println("DEBUG UpdateProfileController: Hasil update model: " + success);
        System.out.println("DEBUG UpdateProfileController: Path foto yang coba disimpan ke model: " + finalProfilePicturePathToSave);

        if (success) {
            JOptionPane.showMessageDialog(view.getUpdateProfileFrame(), "Profil berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            // Update path foto saat ini di controller ini agar konsisten jika view dibuka lagi tanpa re-instansiasi controller
            this.currentProfilePicturePathOnLoad = finalProfilePicturePathToSave; 
            this.newSelectedProfilePicturePath = null; 

            // PENTING: Beri tahu ProfileController parent untuk memuat ulang data dan mengupdate view-nya
            if (parentProfileController != null) {
                System.out.println("DEBUG UpdateProfileController: Memanggil updateUserProfileData() pada parentProfileController dengan email: " + currentUserEmail);
                parentProfileController.updateUserProfileData(currentUserEmail); // Ini akan memanggil loadUserProfile di ProfileController
            } else {
                System.err.println("PERINGATAN di UpdateProfileController: parentProfileController adalah null, tidak bisa refresh ProfileView utama secara otomatis.");
                // Fallback jika tidak ada referensi langsung ke ProfileController
                if (parentProfileView != null) {
                    System.out.println("DEBUG UpdateProfileController: Merefresh ProfileView parent secara langsung (fallback). Username: " + newUsername + ", Path Foto: " + finalProfilePicturePathToSave);
                    parentProfileView.setUsername(newUsername); 
                    parentProfileView.loadProfilePicture(finalProfilePicturePathToSave);
                }
            }
            
            view.getUpdateProfileFrame().dispose();
            if (parentProfileView != null) {
                parentProfileView.setVisible(true); 
            }

        } else {
            JOptionPane.showMessageDialog(view.getUpdateProfileFrame(), "Gagal mengupdate profil. Coba lagi atau periksa konsol.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}