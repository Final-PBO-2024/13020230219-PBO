package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Cursor;

public class AuthView {
    private JFrame loginFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton; 
    private JLabel statusLabel;

    private JFrame signupFrame;
    private JTextField signupUsernameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    private JButton confirmSignupButton;
    private JButton backButton;    
    private JLabel signupStatusLabel;

    private final Color COLOR_BACKGROUND_LIGHT = new Color(245, 247, 250);
    private final Color COLOR_PANEL_DARK_ACCENT = new Color(68, 78, 96);

    private final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;
    
    private final Color COLOR_PRIMARY_ACTION_BG = new Color(0, 123, 255);
    private final Color COLOR_BUTTON_TEXT_LIGHT = Color.WHITE;

    private final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 30);
    private final Font FONT_SUBTITLE_MEDIUM = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_LABEL_FIELD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_FIELD_INPUT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BUTTON_GENERAL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_ACTION_BUTTON = new Font("Segoe UI", Font.BOLD, 13);


    public AuthView() {
        initLoginFrameUI();
        initSignupFrameUI();
        loginFrame.setVisible(true);
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(FONT_ACTION_BUTTON);
        button.setBackground(COLOR_PRIMARY_ACTION_BG);
        button.setForeground(COLOR_BUTTON_TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleOutlinedButton(JButton button, Color panelBackgroundColor) {
        button.setFont(FONT_BUTTON_GENERAL);
        button.setBackground(panelBackgroundColor);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 35, 10, 35)
        ));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private void initLoginFrameUI() {
        loginFrame = new JFrame("Application Login");
        loginFrame.setSize(900, 600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new GridLayout(1, 2));

        JPanel leftPanel = createSignInPanel();
        JPanel rightPanel = createSignUpActionPanelLogin();

        loginFrame.add(leftPanel);
        loginFrame.add(rightPanel);
    }

    private JPanel createSignInPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_BACKGROUND_LIGHT);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel signInTitle = new JLabel("SIGN IN");
        signInTitle.setFont(FONT_TITLE_LARGE);
        signInTitle.setForeground(COLOR_TEXT_DARK);
        signInTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(signInTitle, gbc);

        JLabel signInSubtitle = new JLabel("Welcome back! Please sign in to your account.");
        signInSubtitle.setFont(FONT_SUBTITLE_MEDIUM);
        signInSubtitle.setForeground(Color.GRAY);
        signInSubtitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(signInSubtitle, gbc);

        JLabel emailLabelText = new JLabel("Email Address");
        emailLabelText.setFont(FONT_LABEL_FIELD);
        emailLabelText.setForeground(COLOR_TEXT_DARK);
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailLabelText, gbc);

        emailField = new JTextField(20);
        emailField.setFont(FONT_FIELD_INPUT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(emailField, gbc);

        JLabel passwordLabelText = new JLabel("Password");
        passwordLabelText.setFont(FONT_LABEL_FIELD);
        passwordLabelText.setForeground(COLOR_TEXT_DARK);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(passwordLabelText, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(FONT_FIELD_INPUT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(passwordField, gbc);

        loginButton = new JButton("LOGIN");
        stylePrimaryButton(loginButton);
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(loginButton, gbc);

        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(FONT_SUBTITLE_MEDIUM);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(statusLabel, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

    private JPanel createSignUpActionPanelLogin() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PANEL_DARK_ACCENT);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(50, 70, 50, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel signUpTitle = new JLabel("Hello, Friend!");
        signUpTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        signUpTitle.setForeground(COLOR_TEXT_LIGHT);
        signUpTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(signUpTitle, gbc);

        JLabel signUpSubtitle = new JLabel("<html><body style='text-align: center;'>" +
                "Enter your personal details<br>" +
                "and start your journey with us.</body></html>");
        signUpSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        signUpSubtitle.setForeground(new Color(220, 220, 250));
        signUpSubtitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 30, 0);
        panel.add(signUpSubtitle, gbc);

        signupButton = new JButton("SIGN UP");
        styleOutlinedButton(signupButton, COLOR_PANEL_DARK_ACCENT);
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(signupButton, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

    private void initSignupFrameUI() {
        signupFrame = new JFrame("Create Account");
        signupFrame.setSize(900, 700);
        signupFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        signupFrame.setLocationRelativeTo(null);
        signupFrame.setLayout(new GridLayout(1, 2));

        JPanel leftPanelSignInAction = createSignInActionPanelSignUp();
        JPanel rightPanelSignUpForm = createSignUpFormPanel();

        signupFrame.add(leftPanelSignInAction);
        signupFrame.add(rightPanelSignUpForm);
    }

    private JPanel createSignInActionPanelSignUp() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PANEL_DARK_ACCENT);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(50, 70, 50, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("SIGN IN");
        title.setFont(FONT_TITLE_LARGE);
        title.setForeground(COLOR_TEXT_LIGHT);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("<html><body style='text-align: center;'>Already have an account?<br>Sign in to access your existing dashboard.</body></html>");
        subtitle.setFont(FONT_SUBTITLE_MEDIUM);
        subtitle.setForeground(new Color(200, 200, 220));
        subtitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 30, 0);
        panel.add(subtitle, gbc);

        backButton = new JButton("SIGN IN");
        styleOutlinedButton(backButton, COLOR_PANEL_DARK_ACCENT);
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(backButton, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

    private JPanel createSignUpFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_BACKGROUND_LIGHT);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 70, 40, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel signUpTitle = new JLabel("CREATE ACCOUNT");
        signUpTitle.setFont(FONT_TITLE_LARGE);
        signUpTitle.setForeground(COLOR_TEXT_DARK);
        signUpTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(signUpTitle, gbc);

        JLabel signUpSubtitle = new JLabel("Sign up if you want to create a new account.");
        signUpSubtitle.setFont(FONT_SUBTITLE_MEDIUM);
        signUpSubtitle.setForeground(Color.GRAY);
        signUpSubtitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(signUpSubtitle, gbc);

        signupUsernameField = new JTextField(20);
        addFormField(panel, gbc, "Username", signupUsernameField, 2);

        signupEmailField = new JTextField(20);
        addFormField(panel, gbc, "Email", signupEmailField, 4);

        signupPasswordField = new JPasswordField(20);
        addFormField(panel, gbc, "Password", signupPasswordField, 6);

        signupConfirmPasswordField = new JPasswordField(20);
        addFormField(panel, gbc, "Confirm Password", signupConfirmPasswordField, 8);

        confirmSignupButton = new JButton("SIGN UP");
        stylePrimaryButton(confirmSignupButton);
        gbc.gridy = 10;
        gbc.insets = new Insets(25, 0, 10, 0);
        panel.add(confirmSignupButton, gbc);

        signupStatusLabel = new JLabel(" ", JLabel.CENTER);
        signupStatusLabel.setFont(FONT_SUBTITLE_MEDIUM);
        gbc.gridy = 11;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(signupStatusLabel, gbc);

        gbc.gridy = 12;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int gridYPos) {
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL_FIELD);
        label.setForeground(COLOR_TEXT_DARK);
        gbc.gridy = gridYPos;
        gbc.insets = new Insets(15, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        field.setFont(FONT_FIELD_INPUT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridy = gridYPos + 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(field, gbc);
    }

    public JFrame getLoginFrame() { return loginFrame; }
    public JFrame getSignupFrame() { return signupFrame; }
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignupButton() { return signupButton; }
    public JLabel getStatusLabel() { return statusLabel; }
    public JTextField getSignupUsernameField() { return signupUsernameField; }
    public JTextField getSignupEmailField() { return signupEmailField; }
    public JPasswordField getSignupPasswordField() { return signupPasswordField; }
    public JPasswordField getSignupConfirmPasswordField() { return signupConfirmPasswordField; }
    public JButton getConfirmSignupButton() { return confirmSignupButton; }
    public JButton getBackButton() { return backButton; }
    public JLabel getSignupStatusLabel(){ return signupStatusLabel;}

    public void setLoginStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    public void setSignupStatus(String message, Color color) {
        if(signupStatusLabel != null) {
            signupStatusLabel.setText(message);
            signupStatusLabel.setForeground(color);
        }
    }
}