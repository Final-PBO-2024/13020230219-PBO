package View;

import javax.swing.*;
import java.awt.*;

public class AuthView {
    private JFrame loginFrame;
    private JFrame registerFrame;
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel loginStatusLabel;
    private JLabel registerStatusLabel;
    private JButton loginButton;
    private JButton toRegisterButton;
    private JButton registerButton;
    private JButton toLoginButton;

    public AuthView() {
        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 220);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new GridLayout(5, 1, 10, 10));

        JPanel loginUsernamePanel = new JPanel(new FlowLayout());
        loginUsernameField = new JTextField(15);
        loginUsernamePanel.add(new JLabel("Username: "));
        loginUsernamePanel.add(loginUsernameField);

        JPanel loginPasswordPanel = new JPanel(new FlowLayout());
        loginPasswordField = new JPasswordField(15);
        loginPasswordPanel.add(new JLabel("Password: "));
        loginPasswordPanel.add(loginPasswordField);

        JPanel loginButtonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        toRegisterButton = new JButton("Register");
        loginButtonPanel.add(loginButton);
        loginButtonPanel.add(toRegisterButton);

        loginStatusLabel = new JLabel("", SwingConstants.CENTER);

        loginFrame.add(loginUsernamePanel);
        loginFrame.add(loginPasswordPanel);
        loginFrame.add(loginButtonPanel);
        loginFrame.add(loginStatusLabel);

        registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setSize(300, 250);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setLayout(new GridLayout(5, 1, 10, 10));

        JPanel registerUsernamePanel = new JPanel(new FlowLayout());
        registerUsernameField = new JTextField(15);
        registerUsernamePanel.add(new JLabel("Username: "));
        registerUsernamePanel.add(registerUsernameField);

        JPanel registerPasswordPanel = new JPanel(new FlowLayout());
        registerPasswordField = new JPasswordField(15);
        registerPasswordPanel.add(new JLabel("Password: "));
        registerPasswordPanel.add(registerPasswordField);

        JPanel confirmPasswordPanel = new JPanel(new FlowLayout());
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordPanel.add(new JLabel("Confirm Password: "));
        confirmPasswordPanel.add(confirmPasswordField);

        JPanel registerButtonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("Register");
        toLoginButton = new JButton("Back to Login");
        registerButtonPanel.add(registerButton);
        registerButtonPanel.add(toLoginButton);

        registerStatusLabel = new JLabel("", SwingConstants.CENTER);

        registerFrame.add(registerUsernamePanel);
        registerFrame.add(registerPasswordPanel);
        registerFrame.add(confirmPasswordPanel);
        registerFrame.add(registerButtonPanel);
        registerFrame.add(registerStatusLabel);
    }

    public JFrame getLoginFrame() { return loginFrame; }
    public JFrame getRegisterFrame() { return registerFrame; }
    public JTextField getLoginUsernameField() { return loginUsernameField; }
    public JPasswordField getLoginPasswordField() { return loginPasswordField; }
    public JTextField getRegisterUsernameField() { return registerUsernameField; }
    public JPasswordField getRegisterPasswordField() { return registerPasswordField; }
    public JPasswordField getConfirmPasswordField() { return confirmPasswordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getToRegisterButton() { return toRegisterButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getToLoginButton() { return toLoginButton; }

    public void setLoginStatus(String message, Color color) {
        loginStatusLabel.setText(message);
        loginStatusLabel.setForeground(color);
    }

    public void setRegisterStatus(String message, Color color) {
        registerStatusLabel.setText(message);
        registerStatusLabel.setForeground(color);
    }

    public void clearRegisterFields() {
        registerUsernameField.setText("");
        registerPasswordField.setText("");
        confirmPasswordField.setText("");
    }
}