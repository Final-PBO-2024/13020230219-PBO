package Controller;

import Model.AuthModel;
import View.DashboardView;
import View.EditProfileView;
import View.ProfileView;

import javax.swing.*;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfileController {
    private AuthModel model;
    private ProfileView view;
    private DashboardView dashboardView;

    public ProfileController(AuthModel model, ProfileView view, DashboardView dashboardView) {
        this.model = model;
        this.view = view;
        this.dashboardView = dashboardView;
        initController();
    }

    private void initController() {
        view.getBackButton().addActionListener(e -> {
            System.out.println("Tombol DASHBOARD diklik");
            if (view.getProfileFrame().isVisible()) {
                view.getProfileFrame().setVisible(false);
            }
            if (dashboardView != null && dashboardView.getDashboardFrame() != null) {
                dashboardView.getDashboardFrame().setVisible(true);
            }
        });

        view.getFriendsButton().addActionListener(e -> System.out.println("Tombol List Teman diklik"));
        view.getLoginHistoryButton().addActionListener(e -> System.out.println("Tombol Riwayat Login diklik"));
        view.getUserButton().addActionListener(e -> System.out.println("Tombol User diklik"));
        view.getSettingsButton().addActionListener(e -> System.out.println("Tombol Pengaturan diklik"));
        view.getProfileButton().addActionListener(e -> System.out.println("Tombol Profile diklik"));

        // Tambahkan aksi untuk tombol Ubah Profile
        view.getEditProfileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Tombol Ubah Profile diklik");

                // Buka EditProfileView
                EditProfileView editView = new EditProfileView(
                        view.getProfileFrame(),
                        view.getUsernameLabel().getText(),
                        view.getEmailLabel().getText(),
                        view.getPhotoLabel().getText());

                // Aksi untuk tombol Simpan di EditProfileView
                editView.getSaveButton().addActionListener(saveEvent -> {
                    String newUsername = editView.getUsernameField().getText();
                    String newEmail = editView.getEmailField().getText();
                    String photoPath = editView.getPhotoPathLabel().getText();

                    if (newUsername != null && !newUsername.trim().isEmpty() && newEmail != null
                            && !newEmail.trim().isEmpty()) {
                        view.getUsernameLabel().setText("Username: " + newUsername);
                        view.getEmailLabel().setText("Email: " + newEmail);
                        view.getPhotoLabel()
                                .setText("Foto: " + (photoPath.equals("Belum dipilih") ? "belum diunggah" : photoPath));
                        JOptionPane.showMessageDialog(editView.getEditDialog(), "Profile berhasil diperbarui!");
                        editView.getEditDialog().dispose();
                    } else {
                        JOptionPane.showMessageDialog(editView.getEditDialog(),
                                "Username dan Email tidak boleh kosong!");
                    }
                });

                editView.getEditDialog().setVisible(true);
            }
        });
    }

    public ProfileView getProfileView() {
        return view;
    }
}