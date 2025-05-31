package Controller;

import Model.AuthModel;
import Model.DocumentModel;
import View.AuthView;
import View.DashboardView;
import java.awt.Color;
import java.awt.Frame;
import javax.swing.SwingUtilities;

public class AuthController {
    private AuthModel authModel;
    private DocumentModel documentModel;
    private AuthView authView;
    private DashboardView dashboardView;
    private DashboardController dashboardController;

    public AuthController(AuthModel authM, DocumentModel docM, AuthView view) {
        this.authModel = authM;
        this.documentModel = docM;
        this.authView = view;
        this.dashboardView = null;
        this.dashboardController = null;
        initController();
    }

    private void initController() {
        authView.getLoginButton().addActionListener(e -> {
            System.out.println("DEBUG AuthController: Tombol LOGIN utama DITEKAN!");
            try {
                String email = authView.getEmailField().getText().trim();
                String password = new String(authView.getPasswordField().getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    authView.setLoginStatus("Email dan password tidak boleh kosong.", Color.RED);
                    return;
                }

                if (authModel.authenticate(email, password)) {
                    authView.setLoginStatus("Login berhasil!", new Color(0, 128, 0));
                    String retrievedUsername = authModel.getUsernameByEmail(email);
                    String dashboardUsername = (retrievedUsername != null && !retrievedUsername.isEmpty()) ? retrievedUsername : email.split("@")[0];

                    authView.getEmailField().setText("");
                    authView.getPasswordField().setText("");

                    final String finalDashboardUsername = dashboardUsername;
                    final String finalUserEmail = email;

                    if (dashboardView == null) {
                        SwingUtilities.invokeLater(() -> {
                            dashboardView = new DashboardView(finalDashboardUsername);
                            dashboardController = new DashboardController(authModel, documentModel, dashboardView, authView);
                            dashboardController.setCurrentUserEmail(finalUserEmail);
                            authView.getLoginFrame().setVisible(false);
                            dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                            dashboardView.getDashboardFrame().setVisible(true);
                        });
                    } else {
                        dashboardView.updateWelcomeMessage(finalDashboardUsername);
                        if (dashboardController != null) {
                            dashboardController.setCurrentUserEmail(finalUserEmail);
                            dashboardController.loadInitialDashboardData();
                        }
                        authView.getLoginFrame().setVisible(false);
                        dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                        dashboardView.getDashboardFrame().setVisible(true);
                    }
                } else {
                    authView.setLoginStatus("Login gagal! Email atau password salah.", Color.RED);
                }
            } catch (Exception ex) {
                System.err.println("DEBUG AuthController: EXCEPTION di listener tombol Login!");
                ex.printStackTrace();
            }
        });

        authView.getSignupButton().addActionListener(e -> {
            System.out.println("DEBUG AuthController: Tombol SIGN UP (navigasi) DITEKAN!");
            try {
                authView.getLoginFrame().setVisible(false);
                authView.getSignupFrame().setVisible(true);
                authView.setLoginStatus("", Color.BLACK);
                authView.getSignupUsernameField().setText("");
                authView.getSignupEmailField().setText("");
                authView.getSignupPasswordField().setText("");
                authView.getSignupConfirmPasswordField().setText("");
                authView.setSignupStatus("", Color.BLACK);
            } catch (Exception ex) {
                System.err.println("DEBUG AuthController: EXCEPTION di listener tombol Sign Up (navigasi)!");
                ex.printStackTrace();
            }
        });

        authView.getConfirmSignupButton().addActionListener(e -> {
            System.out.println("DEBUG AuthController: Tombol CONFIRM SIGN UP DITEKAN!");
            try {
                String username = authView.getSignupUsernameField().getText().trim();
                String email = authView.getSignupEmailField().getText().trim();
                String password = new String(authView.getSignupPasswordField().getPassword());
                String confirmPassword = new String(authView.getSignupConfirmPasswordField().getPassword());

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    authView.setSignupStatus("Semua field harus diisi!", Color.RED);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    authView.setSignupStatus("Password dan konfirmasi password tidak cocok!", Color.RED);
                    authView.getSignupPasswordField().setText("");
                    authView.getSignupConfirmPasswordField().setText("");
                    authView.getSignupPasswordField().requestFocus();
                    return;
                }

                if (authModel.register(username, email, password)) {
                    authView.getSignupFrame().setVisible(false);
                    authView.getLoginFrame().setVisible(true);
                    authView.setLoginStatus("Sign up berhasil! Silakan login.", new Color(0, 128, 0));
                    authView.getSignupUsernameField().setText("");
                    authView.getSignupEmailField().setText("");
                    authView.getSignupPasswordField().setText("");
                    authView.getSignupConfirmPasswordField().setText("");
                    authView.setSignupStatus("", Color.BLACK);
                } else {
                    authView.setSignupStatus("Sign up gagal! Email mungkin sudah terdaftar atau input tidak valid.", Color.RED);
                    authView.getSignupEmailField().requestFocus();
                    authView.getSignupPasswordField().setText("");
                    authView.getSignupConfirmPasswordField().setText("");
                }
            } catch (Exception ex) {
                System.err.println("DEBUG AuthController: EXCEPTION di listener tombol Confirm Sign Up!");
                ex.printStackTrace();
            }
        });

        authView.getBackButton().addActionListener(e -> {
            System.out.println("DEBUG AuthController: Tombol BACK (dari signup) DITEKAN!");
            try {
                authView.getSignupFrame().setVisible(false);
                authView.getLoginFrame().setVisible(true);
                authView.setLoginStatus("", Color.BLACK);
                authView.setSignupStatus("", Color.BLACK);
                authView.getEmailField().setText("");
                authView.getPasswordField().setText("");
            } catch (Exception ex) {
                System.err.println("DEBUG AuthController: EXCEPTION di listener tombol Back!");
                ex.printStackTrace();
            }
        });
    }

    public AuthView getAuthView() {
        return authView;
    }
}