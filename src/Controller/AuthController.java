package Controller;

import Model.AuthModel;
import View.AuthView;
import View.DashboardView;
import View.DocumentView;
import View.ProfileView;
import javax.swing.*;
import java.awt.*;

public class AuthController {
    private AuthModel model;
    private AuthView view;

    public AuthController(AuthModel model, AuthView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.getLoginButton().addActionListener(e -> {
            String username = view.getLoginUsernameField().getText();
            String password = new String(view.getLoginPasswordField().getPassword());

            if (model.authenticate(username, password)) {
                view.getLoginFrame().setVisible(false);
                DashboardView dashboardView = new DashboardView(username);
                DocumentView documentView = new DocumentView(); // Inisialisasi DocumentView
                ProfileView profileView = new ProfileView("Dashboard"); // Inisialisasi ProfileView
                DashboardController dashboardController = new DashboardController(model, dashboardView, view, documentView, profileView);
                dashboardView.getDashboardFrame().setVisible(true);
            } else {
                view.setLoginStatus("Username atau password salah!", Color.RED);
            }
        });

        view.getToRegisterButton().addActionListener(e -> {
            view.getLoginFrame().setVisible(false);
            view.getRegisterFrame().setVisible(true);
        });

        view.getRegisterButton().addActionListener(e -> {
            String username = view.getRegisterUsernameField().getText();
            String password = new String(view.getRegisterPasswordField().getPassword());
            String confirmPassword = new String(view.getConfirmPasswordField().getPassword());

            if (!password.equals(confirmPassword)) {
                view.setRegisterStatus("Password tidak cocok!", Color.RED);
                return;
            }

            if (model.register(username, password)) {
                view.setRegisterStatus("Registrasi berhasil! Silakan login.", Color.GREEN);
                view.clearRegisterFields();
                view.getRegisterFrame().setVisible(false);
                view.getLoginFrame().setVisible(true);
            } else {
                view.setRegisterStatus("Username sudah ada atau input kosong!", Color.RED);
            }
        });

        view.getToLoginButton().addActionListener(e -> {
            view.getRegisterFrame().setVisible(false);
            view.getLoginFrame().setVisible(true);
            view.clearRegisterFields();
            view.setRegisterStatus("", Color.BLACK);
        });
    }
}