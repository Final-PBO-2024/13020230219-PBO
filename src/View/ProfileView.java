package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ProfileView {
    private JFrame profileFrame;
    private JLabel usernameValueLabel;
    private JLabel emailValueLabel;
    private ProfilePictureCircularLabel profilePictureDisplayLabel; // Gunakan tipe kustom
    private JButton ubahProfileButton;
    private JButton manageDocumentsButton;

    // Tombol Sidebar
    private JButton backToDashboardButton;
    private JButton logoutButton;
    private JLabel sidebarUserLabel;
    private JLabel sidebarProfileIconLabel;

    // Konstanta Warna dan Font
    private final Color COLOR_BACKGROUND_VIEW = Color.WHITE;
    private final Color COLOR_PROFILE_FIELD_BG = new Color(230, 230, 230);
    private final Color COLOR_PROFILE_FIELD_TEXT = new Color(50, 50, 50);
    private final Color COLOR_LABEL_TEXT = new Color(80, 80, 80);
    private final Color COLOR_UBAH_PROFILE_BG = new Color(50, 50, 50);
    private final Color COLOR_UBAH_PROFILE_TEXT = Color.WHITE;

    private final Color COLOR_SIDEBAR_BG = new Color(68, 78, 96);
    private final Color COLOR_SIDEBAR_BUTTON_BG = new Color(238, 238, 238);
    private final Color COLOR_SIDEBAR_BUTTON_HOVER_BG = new Color(220, 220, 225);
    private final Color COLOR_SIDEBAR_BUTTON_TEXT = new Color(50, 50, 70);
    private final Color COLOR_SIDEBAR_USER_TEXT = Color.WHITE;

    private final Color COLOR_DANGER_ACTION_BG = new Color(220, 53, 69);
    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;

    private final Font FONT_PROFILE_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_PROFILE_LABEL = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_PROFILE_VALUE = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_SIDEBAR_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_SIDEBAR_USER = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_UBAH_PROFILE_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_ACTION_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    public ProfileView(String currentUsername, String currentEmail) {
        profileFrame = new JFrame("Profil Pengguna - " + currentUsername);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setSize(1024, 768);
        profileFrame.setLocationRelativeTo(null);
        profileFrame.setLayout(new BorderLayout(0, 0));
        profileFrame.getContentPane().setBackground(COLOR_BACKGROUND_VIEW);

        JPanel sidebar = createSidebarPanel(currentUsername);
        profileFrame.add(sidebar, BorderLayout.WEST);

        JPanel profileContentPanel = createProfileContentPanel(currentUsername, currentEmail);
        profileFrame.add(profileContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel(String username) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COLOR_SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setLayout(new BoxLayout(sidebarHeader, BoxLayout.X_AXIS));
        sidebarHeader.setBackground(COLOR_SIDEBAR_BG);
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        sidebarHeader.setBorder(new EmptyBorder(10, 20, 20, 15));

        sidebarProfileIconLabel = new JLabel();
        try {
            // GANTI "/icons/default_profile_sidebar.png" dengan path ikon Anda yang valid
            // Pastikan ikon ada di classpath jika menggunakan getResource()
            // ImageIcon profileIcon = new ImageIcon(new
            // ImageIcon(getClass().getResource("/icons/default_profile_sidebar.png")).getImage().getScaledInstance(35,
            // 35, Image.SCALE_SMOOTH));
            // sidebarProfileIconLabel.setIcon(profileIcon);
            sidebarProfileIconLabel.setText("[o]"); // Placeholder jika ikon gagal
            sidebarProfileIconLabel.setForeground(Color.WHITE);
            sidebarProfileIconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        } catch (Exception e) {
            sidebarProfileIconLabel.setText("[o]");
            sidebarProfileIconLabel.setForeground(Color.WHITE);
            sidebarProfileIconLabel.setFont(new Font("Arial", Font.BOLD, 24));
            System.err.println("Gagal memuat ikon profil sidebar: " + e.getMessage());
        }
        sidebarProfileIconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        sidebarUserLabel = new JLabel(username);
        sidebarUserLabel.setForeground(COLOR_SIDEBAR_USER_TEXT);
        sidebarUserLabel.setFont(FONT_SIDEBAR_USER);

        sidebarHeader.add(sidebarProfileIconLabel);
        sidebarHeader.add(sidebarUserLabel);
        sidebar.add(sidebarHeader);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        backToDashboardButton = createSidebarButtonStyled(" Dashboard");
        sidebar.add(backToDashboardButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        // --- TAMBAHKAN PEMBUATAN TOMBOL INI ---
        manageDocumentsButton = createSidebarButtonStyled(" Kelola Dokumen");
        sidebar.add(manageDocumentsButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        sidebar.add(Box.createVerticalGlue());

        logoutButton = new JButton(" Keluar");
        styleDangerButton(logoutButton);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        sidebar.add(logoutButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        return sidebar;
    }

    private JPanel createProfileContentPanel(String currentUsername, String currentEmail) {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(COLOR_BACKGROUND_VIEW);
        contentPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 8, 10, 8);

        JLabel accountTitleLabel = new JLabel("Account");
        accountTitleLabel.setFont(FONT_PROFILE_TITLE);
        accountTitleLabel.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(accountTitleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 8, 10, 8);

        profilePictureDisplayLabel = new ProfilePictureCircularLabel(); // Inisialisasi di sini
        loadProfilePicture(null); // Panggil setelah inisialisasi dengan path default/null
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 25, 8);
        contentPanel.add(profilePictureDisplayLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 8, 10, 8);

        JLabel usernameStaticLabel = new JLabel("Username");
        usernameStaticLabel.setFont(FONT_PROFILE_LABEL);
        usernameStaticLabel.setForeground(COLOR_LABEL_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(usernameStaticLabel, gbc);

        usernameValueLabel = new JLabel(currentUsername != null ? currentUsername : "N/A");
        JPanel usernamePanel = createValuePanel(usernameValueLabel);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contentPanel.add(usernamePanel, gbc);
        gbc.weightx = 0;

        JLabel emailStaticLabel = new JLabel("Email");
        emailStaticLabel.setFont(FONT_PROFILE_LABEL);
        emailStaticLabel.setForeground(COLOR_LABEL_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(emailStaticLabel, gbc);

        emailValueLabel = new JLabel(currentEmail != null ? currentEmail : "N/A");
        JPanel emailPanel = createValuePanel(emailValueLabel);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contentPanel.add(emailPanel, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        contentPanel.add(new JLabel(), gbc); // Spacer
        gbc.weighty = 0;
        gbc.gridwidth = 1;

        ubahProfileButton = new JButton("Ubah Profile");
        styleUbahProfileButton(ubahProfileButton);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(30, 8, 10, 8);
        contentPanel.add(ubahProfileButton, gbc);

        return contentPanel;
    }

    private JPanel createValuePanel(JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(COLOR_PROFILE_FIELD_BG);
        valueLabel.setFont(FONT_PROFILE_VALUE);
        valueLabel.setForeground(COLOR_PROFILE_FIELD_TEXT);
        panel.add(valueLabel);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(5, 10, 5, 10)));
        return panel;
    }

    public void loadProfilePicture(String imagePath) {
        System.out.println("DEBUG ProfileView.loadProfilePicture: Menerima path: " + imagePath);
        if (profilePictureDisplayLabel == null) { // Tambahan null check penting
            System.err.println("ERROR di ProfileView.loadProfilePicture: profilePictureDisplayLabel adalah null!");
            return;
        }
        profilePictureDisplayLabel.setImagePath(imagePath); // Panggil method di inner class
    }

    // Inner class untuk menampilkan gambar profil sebagai lingkaran
    private class ProfilePictureCircularLabel extends JLabel {
        private Image image;
        private final int DIAMETER = 150;

        public ProfilePictureCircularLabel() {
            setPreferredSize(new Dimension(DIAMETER, DIAMETER));
            setOpaque(false);
        }

        public void setImagePath(String path) {
            try {
                if (path != null && !path.isEmpty()) {
                    File imgFile = new File(path);
                    if (imgFile.exists() && imgFile.isFile()) {
                        BufferedImage originalImage = ImageIO.read(imgFile);
                        // Skala dan crop ke tengah untuk lingkaran
                        this.image = createCircularImage(originalImage, DIAMETER);
                    } else {
                        System.err.println(
                                "File gambar profil tidak ditemukan atau bukan file (ProfilePictureCircularLabel): "
                                        + path);
                        this.image = null;
                    }
                } else {
                    this.image = null;
                }
            } catch (IOException e) {
                this.image = null;
                System.err.println("Gagal load gambar di ProfilePictureCircularLabel: " + e.getMessage());
            }
            repaint(); // Panggil repaint setelah image di-set
        }

        private BufferedImage createCircularImage(BufferedImage originalImage, int diameter) {
            BufferedImage circleBuffer = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleBuffer.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, diameter, diameter);
            g2.setComposite(AlphaComposite.SrcOver);
            // g2.setColor(Color.WHITE); // Tidak perlu jika SrcAtop digunakan dengan benar
            // g2.fill(new Ellipse2D.Float(0, 0, diameter, diameter)); // Tidak perlu fill
            // putih dulu

            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));

            int oWidth = originalImage.getWidth();
            int oHeight = originalImage.getHeight();
            int newWidth, newHeight, xOff, yOff;

            // Scale to fill, crop to center (aspect fill)
            if ((float) oWidth / oHeight > 1.0f) { // gambar lebih lebar dari tinggi
                newHeight = diameter;
                newWidth = (diameter * oWidth) / oHeight;
                xOff = (diameter - newWidth) / 2;
                yOff = 0;
            } else { // gambar lebih tinggi dari lebar atau sama
                newWidth = diameter;
                newHeight = (diameter * oHeight) / oWidth;
                xOff = 0;
                yOff = (diameter - newHeight) / 2;
            }
            g2.drawImage(originalImage, xOff, yOff, newWidth, newHeight, null);
            g2.dispose();
            return circleBuffer;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Hapus ini jika setOpaque(false) dan Anda menggambar background sendiri
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Jika setOpaque(false), kita mungkin perlu mengisi background parent dulu
            // g2d.setColor(getParent().getBackground());
            // g2d.fillRect(0,0,getWidth(),getHeight());

            if (image != null) {
                g2d.drawImage(image, 0, 0, DIAMETER, DIAMETER, this);
            } else {
                // Gambar placeholder jika tidak ada gambar
                Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, DIAMETER - 1, DIAMETER - 1);
                g2d.setColor(new Color(200, 200, 200));
                g2d.fill(circle);
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                String placeholderText = "Foto";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(placeholderText);
                g2d.drawString(placeholderText, (DIAMETER - textWidth) / 2,
                        DIAMETER / 2 + fm.getAscent() / 2 - fm.getDescent());
                g2d.setColor(new Color(150, 150, 150));
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(circle);
            }
            g2d.dispose();
        }
    }

    private JButton createSidebarButtonStyled(String text) {
        JButton button = new JButton(text);
        button.setForeground(COLOR_SIDEBAR_BUTTON_TEXT);
        button.setBackground(COLOR_SIDEBAR_BUTTON_BG);
        button.setFont(FONT_SIDEBAR_BUTTON);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SIDEBAR_BUTTON_HOVER_BG);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SIDEBAR_BUTTON_BG);
            }
        });
        return button;
    }

    private void styleDangerButton(JButton button) {
        button.setFont(FONT_ACTION_BUTTON);
        button.setBackground(COLOR_DANGER_ACTION_BG);
        button.setForeground(COLOR_BUTTON_TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleUbahProfileButton(JButton button) {
        button.setFont(FONT_UBAH_PROFILE_BUTTON);
        button.setBackground(COLOR_UBAH_PROFILE_BG);
        button.setForeground(COLOR_UBAH_PROFILE_TEXT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_UBAH_PROFILE_BG, 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Getters
    public JFrame getProfileFrame() {
        return profileFrame;
    }

    public JButton getBackToDashboardButton() {
        return backToDashboardButton;
    }

    public JButton getUbahProfileButton() {
        return ubahProfileButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JLabel getUsernameValueLabel() {
        return usernameValueLabel;
    }

    public JLabel getEmailValueLabel() {
        return emailValueLabel;
    }

    public ProfilePictureCircularLabel getProfilePictureDisplayLabel() {
        return profilePictureDisplayLabel;
    } // Ubah tipe return

    public JButton getManageDocumentsButton() {
        return manageDocumentsButton;
    }

    // Setters
    public void setUsername(String username) {
        usernameValueLabel.setText(username != null ? username : "N/A");
        if (sidebarUserLabel != null) {
            sidebarUserLabel.setText(username != null ? username : "Pengguna");
        }
    }

    public void setEmail(String email) {
        emailValueLabel.setText(email != null ? email : "N/A");
    }

    public void setVisible(boolean visible) {
        profileFrame.setVisible(visible);
        if (visible) {
            profileFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }
}