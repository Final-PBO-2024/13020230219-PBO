// File: Controller/DashboardController.java
package Controller;

import Model.AuthModel;
import Model.Document;
import Model.DocumentModel; 
import View.AuthView;
import View.DashboardView;
import View.DocumentView; 
import View.ProfileView;       
import View.SharedDocumentView; 
import Controller.ProfileController; 
import Controller.SharedDocumentController; 
import Controller.DocumentController; 

import java.awt.Color;
import java.awt.Frame; 
import java.util.Map;
import javax.swing.JOptionPane;
// import javax.swing.SwingUtilities; 

public class DashboardController {
    private AuthModel authModel;         
    private DocumentModel documentModel; 
    private DashboardView dashboardView;
    private AuthView authView; 
    private String currentUserEmail;

    private DocumentView documentView; 
    private DocumentController documentController; 
    private ProfileView profileView;
    private ProfileController profileController;
    private SharedDocumentView sharedDocumentView;
    private SharedDocumentController sharedDocumentController;

    public DashboardController(AuthModel authM, DocumentModel docM, DashboardView dashView, AuthView authV) {
        this.authModel = authM;
        this.documentModel = docM; 
        this.dashboardView = dashView;
        this.authView = authV;
        initDashboardListeners();
    }
    
    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        if (this.currentUserEmail == null || this.currentUserEmail.trim().isEmpty()) {
            System.err.println("PERINGATAN DashboardController: currentUserEmail tidak valid atau belum di-set.");
            return; 
        }
        loadInitialDashboardData(); 
    }

    private void initDashboardListeners() {
        if (dashboardView.getLogoutButton() != null) {
            dashboardView.getLogoutButton().addActionListener(e -> handleLogout());
        } // ... (sisa listener tetap sama) ...

        if (dashboardView.getDocumentButton() != null) { 
            dashboardView.getDocumentButton().addActionListener(e -> handleNavigateToDocumentView());
        }
        if (dashboardView.getRefreshButton() != null) { 
            dashboardView.getRefreshButton().addActionListener(e -> handleRefreshActivityTable());
        }
        if (dashboardView.getRecycleBinButton() != null) { 
            dashboardView.getRecycleBinButton().addActionListener(e -> showRecycleBinPanel());
        }
        if (dashboardView.getRestoreButton() != null) { 
            dashboardView.getRestoreButton().addActionListener(e -> handleRestoreDocument());
        }
        if (dashboardView.getPermanentDeleteButton() != null) { 
            dashboardView.getPermanentDeleteButton().addActionListener(e -> handlePermanentDeleteDocument());
        }
        if (dashboardView.getProfileButton() != null) { 
            dashboardView.getProfileButton().addActionListener(e -> handleNavigateToProfileView());
        }
        if (dashboardView.getSharedDocumentsButton() != null) { 
            dashboardView.getSharedDocumentsButton().addActionListener(e -> handleNavigateToSharedDocuments());
        }
    }
    
    public void loadInitialDashboardData() {
        if (dashboardView == null || documentModel == null || authModel == null) { 
            System.err.println("DashboardController: View atau salah satu Model belum diinisialisasi untuk loadInitialDashboardData.");
            return;
        }
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
             System.err.println("DashboardController: currentUserEmail belum di-set, tidak bisa memuat data dashboard spesifik pengguna.");
             dashboardView.clearActivityTable();
             dashboardView.updateInfoCards(0,0); 
             return;
        }

        dashboardView.clearActivityTable(); 
        Map<String, Document> activeDocs = documentModel.getActiveDocuments(); 
        dashboardView.updateInfoCards(activeDocs.size(), calculateTotalAccess(activeDocs)); 

        if (activeDocs != null) {
            activeDocs.values().forEach(doc -> {
                // Pastikan currentUserEmail adalah pemilik atau memiliki akses sebelum ditampilkan di 'Activity'
                // Atau sesuaikan logika ini jika 'Activity' menampilkan semua dokumen aktif
                // Untuk sekarang, kita asumsikan semua dokumen aktif bisa ditampilkan di sini.
                String ownerUsername = authModel.getUsernameByEmail(doc.getOwnerEmail()); 
                if (ownerUsername == null || ownerUsername.isEmpty()) {
                    ownerUsername = doc.getOwnerEmail() != null ? doc.getOwnerEmail().split("@")[0] : "N/A";
                }
                dashboardView.addActivity(
                    "Dokumen Tersedia", 
                    ownerUsername,
                    doc.getName(), 
                    doc.getTimestamp() != null && doc.getTimestamp().contains(" ") ? doc.getTimestamp().split(" ")[0] : doc.getTimestamp(), 
                    doc.getTimestamp() != null && doc.getTimestamp().split(" ").length > 1 ? doc.getTimestamp().split(" ")[1] : "-" 
                );
            });
        }
    }
    
    private int calculateTotalAccess(Map<String, Document> docs) {
        int totalAccess = 0;
        if (docs != null) {
            for (Document doc : docs.values()) {
                if (doc.getAccessListWithTimestamp() != null) { 
                    // Menghitung jumlah unique user yang punya akses (selain owner)
                    long distinctAccessUsers = doc.getAccessListWithTimestamp().keySet().stream()
                                                .filter(email -> !email.equals(doc.getOwnerEmail()))
                                                .count();
                    totalAccess += distinctAccessUsers;
                }
            }
        }
        return totalAccess;
    }

    private void showRecycleBinPanel() { 
        dashboardView.showPanel("RecycleBinPanel");
        loadRecycledDocuments();
    }

    private void loadRecycledDocuments() {
        if (dashboardView == null || documentModel == null) return; 
        dashboardView.clearRecycleBinTable();
        // Filter recycledDocs untuk hanya menampilkan dokumen milik currentUserEmail
        Map<String, Document> allRecycledDocs = documentModel.getRecycledDocuments(); 
        boolean foundDocsForUser = false;
        if (allRecycledDocs != null && currentUserEmail != null) {
            for (Document doc : allRecycledDocs.values()) {
                if (currentUserEmail.equals(doc.getOwnerEmail())) {
                    dashboardView.addRecycledDocumentToTable(
                        doc.getName(),
                        doc.getOwnerEmail(), // Tetap tampilkan owner email
                        doc.getTimestamp(),  // Ini adalah deleted_timestamp
                        doc.getFilePath()    // Path mungkin masih relevan untuk info
                    );
                    foundDocsForUser = true;
                }
            }
        }
        if (!foundDocsForUser) {
            dashboardView.addRecycledDocumentToTable("Recycle Bin Anda kosong.", "", "", "");
        }
    }

    private void handleRestoreDocument() {
        int selectedRow = dashboardView.getSelectedRecycleBinTableRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Pilih dokumen yang akan direstore.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        // Ambil nama dan owner dari tabel recycle bin di view
        String docName = dashboardView.getDocumentNameFromRecycleBinTable(selectedRow);
        String ownerEmail = dashboardView.getOwnerEmailFromRecycleBinTable(selectedRow); // Anda perlu method ini di DashboardView

        if (docName == null || docName.equals("Recycle Bin Anda kosong.") || ownerEmail == null) {
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Data dokumen tidak valid untuk direstore.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Pastikan hanya pemilik yang bisa restore (meskipun recycle bin sudah difilter)
        if (!currentUserEmail.equals(ownerEmail)) {
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Anda hanya bisa merestore dokumen milik Anda.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (documentModel.restoreDocument(docName, ownerEmail)) { 
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Dokumen '" + docName + "' berhasil direstore.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadRecycledDocuments(); 
            loadInitialDashboardData(); // Refresh data dashboard utama juga
        } else { 
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Gagal merestore dokumen '" + docName + "'. Mungkin nama yang sama sudah ada di dokumen aktif.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePermanentDeleteDocument() {
        int selectedRow = dashboardView.getSelectedRecycleBinTableRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Pilih dokumen yang akan dihapus permanen.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        String docName = dashboardView.getDocumentNameFromRecycleBinTable(selectedRow);
        String ownerEmail = dashboardView.getOwnerEmailFromRecycleBinTable(selectedRow); // Anda perlu method ini di DashboardView

        if (docName == null || docName.equals("Recycle Bin Anda kosong.") || ownerEmail == null) {
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Data dokumen tidak valid untuk dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!currentUserEmail.equals(ownerEmail)) {
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Anda hanya bisa menghapus permanen dokumen milik Anda.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(dashboardView.getDashboardFrame(),
                "Apakah Anda yakin ingin menghapus '" + docName + "' secara permanen?\nAksi ini tidak bisa dibatalkan dan file fisik akan dihapus.",
                "Konfirmasi Hapus Permanen", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (documentModel.permanentlyDeleteDocument(docName, ownerEmail)) { 
                JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Dokumen '" + docName + "' berhasil dihapus permanen.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadRecycledDocuments(); 
            } else { 
                JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Gagal menghapus dokumen '" + docName + "' secara permanen.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleNavigateToDocumentView() {
        dashboardView.getDashboardFrame().setVisible(false);
        if (documentView == null) {
            documentView = new DocumentView();
            // Teruskan this (DashboardController) jika DocumentController perlu memanggil method di sini
            documentController = new DocumentController(documentView, dashboardView, authModel, documentModel, this); 
        }
        documentController.setCurrentUser(this.currentUserEmail); 
        documentView.getDocumentFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        documentView.getDocumentFrame().setVisible(true);
    }

    private void handleNavigateToProfileView() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) { 
            JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Tidak dapat membuka profil, data pengguna tidak valid.", "Error Profil", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        dashboardView.getDashboardFrame().setVisible(false); 
        String username = authModel.getUsernameByEmail(currentUserEmail); 
        if (username == null || username.isEmpty()) {
            username = currentUserEmail.split("@")[0]; 
        }
        
        String currentProfilePicturePath = authModel.getUserProfilePicturePath(currentUserEmail); 
        if (currentProfilePicturePath == null || currentProfilePicturePath.isEmpty()) {
            // Path default bisa di-set di sini atau di ProfileView/Controller
            currentProfilePicturePath = "placeholder/default_avatar.png"; 
        }

        if (profileView == null) {
            profileView = new ProfileView(username, currentUserEmail);
            // Teruskan this (DashboardController) jika ProfileController perlu callback
            profileController = new ProfileController(profileView, authModel, dashboardView, this, currentUserEmail);
        } else {
            // profileView.setUsername(username); // Sebaiknya di-handle oleh ProfileController.updateUserProfileData
            // profileView.setEmail(currentUserEmail);
            if (profileController != null) {
                profileController.updateUserProfileData(currentUserEmail); // Ini akan load ulang data
            }
        }
        // profileView.loadProfilePicture(currentProfilePicturePath); // Biarkan ProfileController yang handle ini saat update
        profileView.getProfileFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        profileView.setVisible(true); 
    }
    
    private void handleNavigateToSharedDocuments() {
        dashboardView.getDashboardFrame().setVisible(false);
        String username = authModel.getUsernameByEmail(currentUserEmail); 
        if (username == null) username = currentUserEmail.split("@")[0];

        if (sharedDocumentView == null) {
            sharedDocumentView = new SharedDocumentView(username);
            // Teruskan this (DashboardController) jika perlu
            sharedDocumentController = new SharedDocumentController(sharedDocumentView, authModel, documentModel, dashboardView, this, currentUserEmail);
        } else {
            sharedDocumentController.setCurrentUserEmail(currentUserEmail); 
            sharedDocumentController.refreshData(); 
        }
        // Pastikan frame dimaksimalkan sebelum visible
        sharedDocumentView.getSharedFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        sharedDocumentView.setVisible(true); 
    }

    private void handleLogout() {
        // Sembunyikan semua view yang mungkin terbuka
        if (dashboardView != null && dashboardView.getDashboardFrame() != null) dashboardView.getDashboardFrame().setVisible(false);
        if (documentView != null && documentView.getDocumentFrame() != null) documentView.getDocumentFrame().dispose(); // Dispose agar state-nya bersih
        if (profileView != null && profileView.getProfileFrame() != null) profileView.getProfileFrame().dispose();
        if (sharedDocumentView != null && sharedDocumentView.getSharedFrame() != null) sharedDocumentView.getSharedFrame().dispose();
        
        // Reset instance view dan controller agar dibuat baru saat login berikutnya
        documentView = null; documentController = null;
        profileView = null; profileController = null;
        sharedDocumentView = null; sharedDocumentController = null;
        // dashboardView dan dashboardController tidak di-reset di sini karena AuthController yang mengelola lifecycle-nya jika diperlukan.
        // Namun, currentUserEmail perlu dibersihkan dari instance DashboardController saat ini jika mau.
        // this.currentUserEmail = null; // Atau biarkan AuthController yang menangani state setelah logout.


        if (authView != null) {
            authView.getLoginFrame().setVisible(true);
            authView.getEmailField().setText("");
            authView.getPasswordField().setText("");
            authView.setLoginStatus("Anda telah keluar.", Color.BLUE);
        } else { 
            System.err.println("Error: AuthView tidak diinisialisasi di DashboardController untuk logout.");
            // Sebagai fallback darurat jika AuthView hilang
            System.exit(0); 
        }
    }

    private void handleRefreshActivityTable() {
        loadInitialDashboardData(); 
        JOptionPane.showMessageDialog(dashboardView.getDashboardFrame(), "Data aktivitas telah disegarkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Metode ini bisa dipanggil oleh controller anak untuk kembali ke dashboard
    // dan memastikan data dashboard di-refresh.
    public void navigateBackToDashboardAndRefresh() {
        if(documentView != null) documentView.getDocumentFrame().setVisible(false);
        if(profileView != null) profileView.getProfileFrame().setVisible(false);
        if(sharedDocumentView != null) sharedDocumentView.getSharedFrame().setVisible(false);
        // Tambahkan view lain jika ada

        dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        dashboardView.getDashboardFrame().setVisible(true);
        loadInitialDashboardData(); // Refresh data dashboard
    }
}