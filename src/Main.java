/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.SwingUtilities;

import Model.AuthModel;
import View.AuthView;
import Controller.AuthController;

/**
 *
 * @author ASUS
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthModel model = new AuthModel();
            AuthView view = new AuthView();
            AuthController controller = new AuthController(model, view);
            view.getLoginFrame().setVisible(true);
        });
    }
}
