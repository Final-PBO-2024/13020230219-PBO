package View;

import javax.swing.*;
import java.awt.*;

public class ProfileView {
    private JFrame profileFrame;
    private JButton backButton;
    private JButton editProfileButton;
    private JButton friendsButton;
    private JButton loginHistoryButton;
    private JButton userButton;
    private JButton settingsButton;
    private JButton profileButton;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel photoLabel;
    private String returnView;

    public ProfileView(String returnView) {
        this.returnView = returnView;
        profileFrame = new JFrame("Profile");
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setSize(400, 400);
        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(false);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, 0));

        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 16));
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(adminLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        backButton = new JButton("DASHBOARD");
        backButton.setBackground(new Color(30, 30, 30));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(backButton);

        friendsButton = new JButton("List Teman");
        friendsButton.setBackground(new Color(30, 30, 30));
        friendsButton.setForeground(Color.WHITE);
        friendsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        friendsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(friendsButton);

        loginHistoryButton = new JButton("Riwayat Login");
        loginHistoryButton.setBackground(new Color(30, 30, 30));
        loginHistoryButton.setForeground(Color.WHITE);
        loginHistoryButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginHistoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(loginHistoryButton);

        userButton = new JButton("User");
        userButton.setBackground(new Color(30, 30, 30));
        userButton.setForeground(Color.WHITE);
        userButton.setFont(new Font("Arial", Font.PLAIN, 14));
        userButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userButton);

        settingsButton = new JButton("Pengaturan");
        settingsButton.setBackground(new Color(30, 30, 30));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(settingsButton);

        profileButton = new JButton("Profile");
        profileButton.setBackground(new Color(30, 30, 30));
        profileButton.setForeground(Color.WHITE);
        profileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        profileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(profileButton);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton logoutButton = new JButton("Keluar");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoutButton);

        // Panel utama untuk profil
        JPanel profilePanel = new JPanel(new GridLayout(6, 1));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Profile Pengguna");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(titleLabel);

        usernameLabel = new JLabel("Username: user123");
        usernameLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(usernameLabel);

        emailLabel = new JLabel("Email: user@example.com");
        emailLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(emailLabel);

        photoLabel = new JLabel("Foto: belum diunggah");
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(photoLabel);

        editProfileButton = new JButton("Ubah Profile");
        profilePanel.add(editProfileButton);

        profileFrame.add(sidebar, BorderLayout.WEST);
        profileFrame.add(profilePanel, BorderLayout.CENTER);
    }

    public JFrame getProfileFrame() {
        return profileFrame;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getEditProfileButton() {
        return editProfileButton;
    }

    public JButton getFriendsButton() {
        return friendsButton;
    }

    public JButton getLoginHistoryButton() {
        return loginHistoryButton;
    }

    public JButton getUserButton() {
        return userButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getProfileButton() {
        return profileButton;
    }

    public JLabel getUsernameLabel() {
        return usernameLabel;
    }

    public JLabel getEmailLabel() {
        return emailLabel;
    }

    public JLabel getPhotoLabel() {
        return photoLabel;
    }
}