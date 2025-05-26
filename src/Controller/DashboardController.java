package Controller;

import Model.AuthModel;
import View.DashboardView;
import View.DocumentView;
import View.ProfileView;
import View.AuthView;
import java.awt.Color;
import javax.swing.*;

public class DashboardController {
    private AuthModel model;
    private DashboardView view;
    private AuthView authView;
    private DocumentView documentView;
    private DocumentController documentController;
    private ProfileView profileView;

    public DashboardController(AuthModel model, DashboardView view, AuthView authView, DocumentView documentView, ProfileView profileView) {
        this.model = model;
        this.view = view;
        this.authView = authView;
        this.documentView = documentView; // Gunakan parameter yang diteruskan
        this.documentController = null;
        this.profileView = profileView;
        initController();
    }

    private void initController() {
        // Aksi untuk tombol Keluar (Logout)
        view.getLogoutButton().addActionListener(e -> {
            view.getDashboardFrame().setVisible(false);
            authView.getLoginFrame().setVisible(true);
            authView.setLoginStatus("", Color.BLACK);
        });

        // Aksi untuk tombol Kelola Dokumen
        view.getDocumentButton().addActionListener(e -> {
            System.out.println("Tombol Kelola Dokumen diklik");
            if (documentView == null) {
                System.out.println("DocumentView tidak tersedia sebagai parameter!");
                JOptionPane.showMessageDialog(view.getDashboardFrame(), "Gagal memuat Kelola Dokumen: DocumentView tidak diinisialisasi!");
                return;
            }
            if (documentController == null) {
                documentController = new DocumentController(model, documentView, view);
                documentController.setAuthView(authView); // Set AuthView untuk logout
                System.out.println("DocumentController diinisialisasi");
            }
            if (documentView.getDocumentFrame() != null) {
                documentView.getDocumentFrame().setVisible(true);
                view.getDashboardFrame().setVisible(false);
                System.out.println("Navigasi ke DocumentView berhasil");
            } else {
                System.out.println("DocumentFrame tidak tersedia");
                JOptionPane.showMessageDialog(view.getDashboardFrame(), "Kelola Dokumen tidak tersedia!");
            }
        });

        // Aksi untuk tombol Profile
        view.getProfileButton().addActionListener(e -> {
            if (profileView == null) {
                profileView = new ProfileView("Dashboard");
                if (profileView.getProfileFrame() == null) {
                    JOptionPane.showMessageDialog(view.getDashboardFrame(), "Profile tidak dapat dimuat!");
                    return;
                }
                new ProfileController(model, profileView, view);
                profileView.getBackButton().addActionListener(backEvent -> {
                    profileView.getProfileFrame().setVisible(false);
                    view.getDashboardFrame().setVisible(true);
                });
            }
            if (profileView.getProfileFrame() != null) {
                profileView.getProfileFrame().setVisible(true);
                view.getDashboardFrame().setVisible(false);
            } else {
                JOptionPane.showMessageDialog(view.getDashboardFrame(), "Profile tidak tersedia!");
            }
        });

        // Aksi untuk tombol Refresh
        view.getRefreshButton().addActionListener(e -> {
            view.refreshTable();
            JOptionPane.showMessageDialog(view.getDashboardFrame(), "Tabel telah disegarkan!");
        });

        // Aksi sementara untuk tombol lain
        view.getFriendsButton().addActionListener(e -> System.out.println("Tombol List Teman diklik"));
        view.getLoginHistoryButton().addActionListener(e -> System.out.println("Tombol Riwayat Login diklik"));
        view.getUserButton().addActionListener(e -> System.out.println("Tombol User diklik"));
        view.getSettingsButton().addActionListener(e -> System.out.println("Tombol Pengaturan diklik"));
    }
}