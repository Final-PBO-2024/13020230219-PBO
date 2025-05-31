package Controller;

import Model.AuthModel;
import View.DashboardView;
import View.ProfileView;
import View.UpdateProfileView; // Import jika belum
import Controller.UpdateProfileController; // Import jika belum
import java.awt.Frame;
import javax.swing.JOptionPane;

public class ProfileController {
    private ProfileView profileView;
    private AuthModel authModel;
    private DashboardView dashboardView;
    private String currentUserEmail;
    private String currentUsername;

    // Tambahkan field untuk UpdateProfileView dan UpdateProfileController
    private UpdateProfileView updateProfileView;
    private UpdateProfileController updateProfileController;

    public ProfileController(ProfileView pView, AuthModel model, DashboardView dView, String userEmail) {
        this.profileView = pView;
        this.authModel = model;
        this.dashboardView = dView;
        this.currentUserEmail = userEmail;

        initProfileViewListeners();
        loadUserProfile();
    }

    private void initProfileViewListeners() {
        if (profileView.getBackToDashboardButton() != null) {
            profileView.getBackToDashboardButton().addActionListener(e -> {
                profileView.setVisible(false);
                if (dashboardView != null) {
                    dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                    dashboardView.getDashboardFrame().setVisible(true);
                    // Jika DashboardController memiliki method loadInitialDashboardData() yang
                    // public
                    // dan Anda memiliki cara untuk mendapatkan instance DashboardController di
                    // sini, Anda bisa memanggilnya.
                    // Object dashController = getDashboardControllerInstance(); // Placeholder
                    // if (dashController instanceof DashboardController) {
                    // ((DashboardController) dashController).loadInitialDashboardData();
                    // }
                } else {
                    System.err.println("Error di ProfileController: Referensi DashboardView null, tidak bisa kembali.");
                    profileView.getProfileFrame().dispose();
                }
            });
        } else {
            System.err
                    .println("Peringatan di ProfileController: Tombol BackToDashboard di ProfileView tidak ditemukan.");
        }

        if (profileView.getUbahProfileButton() != null) {
            profileView.getUbahProfileButton().addActionListener(e -> handleNavigateToUpdateProfileView());
        } else {
            System.err.println("Peringatan di ProfileController: Tombol Ubah Profile di ProfileView tidak ditemukan.");
        }

        if (profileView.getLogoutButton() != null) {
            profileView.getLogoutButton().addActionListener(e -> {
                System.out.println("DEBUG: Tombol Logout dari ProfileView ditekan.");
                profileView.setVisible(false);

                if (dashboardView != null) {
                    dashboardView.getDashboardFrame().setVisible(false);

                    if (dashboardView.getLogoutButton().getActionListeners().length > 0) {
                        dashboardView.getLogoutButton().doClick();
                    } else {
                        System.err.println(
                                "Tidak bisa memicu logout dari DashboardController via ProfileController. Kembali ke AuthView secara manual (jika ada referensi) atau exit.");
                        // Jika AuthView bisa diakses dari sini (TIDAK IDEAL):
                        // AuthView authViewInstance = getAuthViewInstance(); // Perlu cara mendapatkan
                        // ini
                        // if (authViewInstance != null)
                        // authViewInstance.getLoginFrame().setVisible(true);
                        // else System.exit(0);
                        System.exit(0); // Darurat
                    }
                } else {
                    System.err.println(
                            "DashboardView null, tidak bisa logout dengan benar dari ProfileView. Menutup aplikasi.");
                    System.exit(0);
                }
            });
        } else {
            System.err.println("Peringatan di ProfileController: Tombol Logout di ProfileView tidak ditemukan.");
        }

        if (profileView.getManageDocumentsButton() != null) {
            profileView.getManageDocumentsButton().addActionListener(e -> {
                System.out.println("DEBUG: Tombol Kelola Dokumen dari ProfileView ditekan.");
                // Logika navigasi: Sembunyikan ProfileView, tampilkan DashboardView,
                // lalu minta DashboardController untuk navigasi ke DocumentView.
                profileView.setVisible(false);
                if (dashboardView != null) {
                    dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                    dashboardView.getDashboardFrame().setVisible(true);

                    // Cara untuk memicu navigasi ke DocumentView dari DashboardController
                    // Ini memerlukan DashboardController memiliki method publik untuk ini,
                    // atau tombol Document di DashboardView di-trigger.
                    Object dashController = getDashboardControllerInstance(); // Anda perlu cara mendapatkan ini
                    if (dashController instanceof DashboardController) {
                        // Asumsi DashboardController punya method untuk langsung ke DocumentView
                        // ((DashboardController) dashController).handleNavigateToDocumentView();
                        // Atau trigger tombolnya
                        if (dashboardView.getDocumentButton() != null
                                && dashboardView.getDocumentButton().getActionListeners().length > 0) {
                            dashboardView.getDocumentButton().doClick();
                        } else {
                            JOptionPane.showMessageDialog(profileView.getProfileFrame(),
                                    "Fungsi Kelola Dokumen belum siap dari sini.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(profileView.getProfileFrame(),
                                "Tidak bisa navigasi ke Kelola Dokumen.");
                    }
                }
            });
        }

    }

    public void loadUserProfile() {
        System.out.println("DEBUG ProfileController: Memulai loadUserProfile untuk email: " + currentUserEmail);
        if (currentUserEmail != null && !currentUserEmail.isEmpty() && authModel != null && profileView != null) {
            this.currentUsername = authModel.getUsernameByEmail(currentUserEmail);

            if (this.currentUsername == null || this.currentUsername.isEmpty()) {
                this.currentUsername = currentUserEmail.split("@")[0];
                System.out.println("Peringatan di ProfileController: Username dari model null untuk email "
                        + currentUserEmail + ". Menggunakan bagian email sebagai fallback.");
            }

            profileView.setUsername(this.currentUsername);
            profileView.setEmail(currentUserEmail);

            String imagePathFromModel = authModel.getUserProfilePicturePath(currentUserEmail);
            System.out.println("DEBUG ProfileController.loadUserProfile: Path foto dari model: " + imagePathFromModel);
            profileView.loadProfilePicture(imagePathFromModel);

        } else {
            if (profileView != null) {
                profileView.setUsername("Data Tidak Tersedia");
                profileView.setEmail("Data Tidak Tersedia");
                profileView.loadProfilePicture(null);
            }
            System.err.println("Tidak bisa memuat profil di ProfileController: data tidak lengkap.");
        }
    }

    public void updateUserProfileData(String userEmail) {
        System.out
                .println("DEBUG ProfileController: Menerima panggilan updateUserProfileData untuk email: " + userEmail);
        this.currentUserEmail = userEmail; // Update email jika berubah (biasanya tidak untuk user yang sama)
        loadUserProfile();
    }

    private void handleNavigateToUpdateProfileView() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            JOptionPane.showMessageDialog(profileView.getProfileFrame(),
                    "Tidak dapat membuka form ubah profil, data pengguna tidak valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        profileView.setVisible(false);

        String username = this.currentUsername; // Gunakan username yang sudah dimuat

        String currentProfilePicturePath = authModel.getUserProfilePicturePath(currentUserEmail);
        if (currentProfilePicturePath == null || currentProfilePicturePath.isEmpty()) {
            currentProfilePicturePath = "placeholder/default_avatar.png"; // Ganti dengan path default Anda
        }

        if (updateProfileView == null) {
            updateProfileView = new UpdateProfileView(username, currentUserEmail, currentProfilePicturePath);
            // Teruskan ProfileController ini (this) ke UpdateProfileController
            updateProfileController = new UpdateProfileController(updateProfileView, authModel, profileView, this,
                    currentUserEmail, currentProfilePicturePath);
        } else {
            updateProfileView.setUsername(username);
            updateProfileView.setEmail(currentUserEmail);
            updateProfileView.loadProfilePicture(currentProfilePicturePath);
            if (updateProfileController != null) {
                // Update juga parent controller jika diperlukan
                updateProfileController.setCurrentUserData(currentUserEmail, currentProfilePicturePath);
            }
        }
        updateProfileView.setVisible(true);
    }

    // Placeholder - bagaimana Anda mendapatkan instance DashboardController akan
    // bergantung pada arsitektur Anda
    private Object getDashboardControllerInstance() {
        return null;
    }
}