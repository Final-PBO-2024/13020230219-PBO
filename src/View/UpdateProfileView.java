// File: View/UpdateProfileView.java
package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UpdateProfileView {
    private JFrame updateProfileFrame; // Nama frame utama, getter akan getUpdateProfileFrame()
    private JLabel profilePictureDisplayLabel;
    private JButton changePictureButton;
    private JButton deletePictureButton;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private JButton saveChangesButton;
    private JButton cancelButton;

    // Warna dan Font
    private final Color COLOR_BACKGROUND = new Color(230, 230, 230);
    private final Color COLOR_PANEL_BG = Color.WHITE;
    private final Color COLOR_FIELD_BG = new Color(70, 70, 70);
    private final Color COLOR_FIELD_TEXT = Color.WHITE;
    private final Color COLOR_LABEL = new Color(80, 80, 80);
    private final Color COLOR_BUTTON_DARK_BG = new Color(50, 50, 50);
    private final Color COLOR_BUTTON_SECONDARY_BG = new Color(108, 117, 125);
    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_LABEL_FIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_FIELD_VALUE = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_BUTTON_ACTION = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_BUTTON_PHOTO = new Font("Segoe UI", Font.PLAIN, 12);

    public UpdateProfileView(String currentUsername, String currentEmail, String currentProfilePicturePath) {
        updateProfileFrame = new JFrame("Ubah Profile: " + currentUsername); // Inisialisasi field frame
        updateProfileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateProfileFrame.setSize(650, 750);
        updateProfileFrame.setLocationRelativeTo(null);
        updateProfileFrame.setLayout(new BorderLayout());
        updateProfileFrame.getContentPane().setBackground(COLOR_BACKGROUND);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_PANEL_BG);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200), 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Ubah Profile");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(new Color(50,50,50));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        profilePictureDisplayLabel = new ProfilePictureCircularLabel();
        loadProfilePicture(currentProfilePicturePath);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 8, 20);
        mainPanel.add(profilePictureDisplayLabel, gbc);
        gbc.gridheight = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        changePictureButton = new JButton("Ubah Foto");
        stylePhotoButton(changePictureButton);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(changePictureButton, gbc);

        deletePictureButton = new JButton("Hapus Foto");
        stylePhotoButton(deletePictureButton);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(deletePictureButton, gbc);
        gbc.gridwidth = 1;

        usernameField = createStyledTextField(currentUsername);
        addFormField(mainPanel, gbc, "Username", usernameField, 3, 0);

        emailField = createStyledTextField(currentEmail);
        emailField.setEditable(false);
        emailField.setBackground(new Color(100,100,100));
        addFormField(mainPanel, gbc, "Email", emailField, 4, 0);

        newPasswordField = createStyledPasswordField();
        addFormField(mainPanel, gbc, "Password Baru", newPasswordField, 5, 0);

        confirmNewPasswordField = createStyledPasswordField();
        addFormField(mainPanel, gbc, "Konfirmasi Password", confirmNewPasswordField, 6, 0);
        
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0,0));
        actionButtonsPanel.setOpaque(false);

        cancelButton = new JButton("Batal");
        styleActionButton(cancelButton, COLOR_BUTTON_SECONDARY_BG);

        saveChangesButton = new JButton("Ubah Sekarang");
        styleActionButton(saveChangesButton, COLOR_BUTTON_DARK_BG);

        actionButtonsPanel.add(cancelButton);
        actionButtonsPanel.add(Box.createRigidArea(new Dimension(10,0)));
        actionButtonsPanel.add(saveChangesButton);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(actionButtonsPanel, gbc);

        updateProfileFrame.add(mainPanel, BorderLayout.CENTER);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int yPos, int xOffsetLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL_FIELD);
        label.setForeground(COLOR_LABEL);
        gbc.gridx = xOffsetLabel + 1; 
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.3; 
        panel.add(label, gbc);
        
        gbc.gridx = xOffsetLabel; 
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7; 
        panel.add(field, gbc);
        gbc.weightx = 0; 
    }

    private JTextField createStyledTextField(String initialText) {
        JTextField textField = new JTextField(initialText, 20);
        textField.setFont(FONT_FIELD_VALUE);
        textField.setBackground(COLOR_FIELD_BG);
        textField.setForeground(COLOR_FIELD_TEXT);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(COLOR_FIELD_BG, 15, 1), // Menggunakan RoundedBorder
            new EmptyBorder(8, 12, 8, 12)
        ));
        textField.setOpaque(true);
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(FONT_FIELD_VALUE);
        passwordField.setBackground(COLOR_FIELD_BG);
        passwordField.setForeground(COLOR_FIELD_TEXT);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(COLOR_FIELD_BG, 15, 1), // Menggunakan RoundedBorder
            new EmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setOpaque(true);
        return passwordField;
    }
    
    private void stylePhotoButton(JButton button) {
        button.setFont(FONT_BUTTON_PHOTO);
        button.setBackground(new Color(100, 100, 100));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(new Color(80,80,80),10,1)); // Menggunakan RoundedBorder
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Dimension prefSize = button.getPreferredSize();
        button.setPreferredSize(new Dimension(Math.max(100, (int)(prefSize.width * 0.9)), (int)(prefSize.height * 0.9)));
        button.setMargin(new Insets(4,8,4,8));
    }

    private void styleActionButton(JButton button, Color backgroundColor) {
        button.setFont(FONT_BUTTON_ACTION);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_BUTTON_TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(backgroundColor, 15,1)); // Menggunakan RoundedBorder
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 40));
    }

    private class ProfilePictureCircularLabel extends JLabel {
        private Image image;
        private final int DIAMETER = 100;

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
                        this.image = createCircularImage(originalImage, DIAMETER);
                    } else { 
                        System.err.println("File gambar profil tidak ditemukan atau bukan file (ProfilePictureCircularLabel): " + path);
                        this.image = null; 
                    }
                } else { 
                    this.image = null; 
                }
            } catch (IOException e) { 
                this.image = null; 
                System.err.println("Gagal load gambar di UpdateProfileView$ProfilePictureCircularLabel: " + e.getMessage());
            }
            repaint();
        }
        
        private BufferedImage createCircularImage(BufferedImage originalImage, int diameter) {
            BufferedImage circleBuffer = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleBuffer.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0,0,diameter,diameter);
            g2.setComposite(AlphaComposite.SrcOver);
            
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            
            int oWidth = originalImage.getWidth();
            int oHeight = originalImage.getHeight();
            int newWidth, newHeight, xOff, yOff;

            if ((float)oWidth/oHeight > 1.0f) { 
                newHeight = diameter;
                newWidth = (diameter * oWidth) / oHeight;
                xOff = (diameter - newWidth) / 2;
                yOff = 0;
            } else {
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
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!isOpaque() && getParent() != null) {
                g2d.setColor(getParent().getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            if (image != null) {
                g2d.drawImage(image, 0, 0, DIAMETER, DIAMETER, this);
            } else {
                Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, DIAMETER -1 , DIAMETER -1);
                g2d.setColor(new Color(200,200,200)); 
                g2d.fill(circle);
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                String placeholderText = "Foto";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(placeholderText);
                g2d.drawString(placeholderText, (DIAMETER - textWidth) / 2, DIAMETER / 2 + fm.getAscent() / 2 - fm.getDescent());
                g2d.setColor(new Color(150,150,150)); 
                g2d.setStroke(new BasicStroke(1)); 
                g2d.draw(circle);
            }
            g2d.dispose();
        }
    }
    
    public void loadProfilePicture(String imagePath) {
        if (profilePictureDisplayLabel instanceof ProfilePictureCircularLabel) { // Pastikan ini benar
            ((ProfilePictureCircularLabel) profilePictureDisplayLabel).setImagePath(imagePath);
        }
    }

    // --- GETTER UNTUK FRAME UTAMA ---
    public JFrame getUpdateProfileFrame() { 
        return updateProfileFrame; 
    }
    // --------------------------------

    // Getters lainnya
    public JLabel getProfilePictureDisplayLabel() { return profilePictureDisplayLabel; }
    public JButton getChangePictureButton() { return changePictureButton; }
    public JButton getDeletePictureButton() { return deletePictureButton; }
    public JTextField getUsernameField() { return usernameField; }
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getNewPasswordField() { return newPasswordField; }
    public JPasswordField getConfirmNewPasswordField() { return confirmNewPasswordField; }
    public JButton getSaveChangesButton() { return saveChangesButton; }
    public JButton getCancelButton() { return cancelButton; }

    // Setters
    public void setUsername(String username) {
        usernameField.setText(username);
    }
    public void setEmail(String email) { 
        emailField.setText(email);
    }
    
    public void setVisible(boolean visible) {
        updateProfileFrame.setVisible(visible);
        if (visible) {
            updateProfileFrame.setLocationRelativeTo(null); 
        }
    }
}