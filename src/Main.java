// package com.yourcompany.app; // Sesuaikan dengan package Anda

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;

import Model.AuthModel;
import Model.DocumentModel;
import View.AuthView;
import Controller.AuthController;
import Database.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("MainApplication: Inisialisasi database dan tabel berhasil atau sudah ada.");
        } catch (RuntimeException e) {
            System.err.println("KRITIKAL: Tidak dapat menginisialisasi infrastruktur database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Gagal menginisialisasi database: " + e.getMessage() + "\nAplikasi akan keluar.",
                "Error Database Kritis",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("PERINGATAN: Gagal menginisialisasi System Look and Feel. (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
            System.err.println("Mencoba menggunakan Nimbus Look and Feel sebagai fallback...");
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                System.err.println("PERINGATAN: Gagal menginisialisasi Nimbus Look and Feel. (" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + ")");
                System.err.println("Aplikasi akan menggunakan Look and Feel Swing default (Metal).");
            }
        }

        SwingUtilities.invokeLater(() -> {
            AuthModel authModel = new AuthModel();
            DocumentModel documentModel = new DocumentModel();
            AuthView authView = new AuthView();
            AuthController authController = new AuthController(authModel, documentModel, authView);
        });
    }
}