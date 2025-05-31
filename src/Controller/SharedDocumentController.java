package Controller;

import Model.AuthModel;
import Model.Document;
import Model.DocumentModel;
import View.DashboardView;
import View.ProfileView;
import View.SharedDocumentView;
import View.UpdateDocumentView; 

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
// import java.util.ArrayList; // Tidak terpakai
// import java.util.List; // Tidak terpakai
import java.util.Map;
import java.awt.Frame; 
import javax.swing.table.DefaultTableModel;

// Impor yang mungkin diperlukan untuk penanganan DOCX
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.util.zip.ZipException;

public class SharedDocumentController {
    private SharedDocumentView view;
    private AuthModel authModel;
    private DocumentModel documentModel;
    private DashboardView dashboardView; // Untuk navigasi kembali
    private DashboardController dashboardController; // Untuk callback
    private String currentUserEmail;

    private ProfileView targetProfileView;
    private ProfileController targetProfileController;

    private UpdateDocumentView updateSharedDocView;
    private UpdateDocumentController updateSharedDocController;


    public SharedDocumentController(SharedDocumentView sView, AuthModel authM, DocumentModel docM, DashboardView dView, DashboardController dCtrl, String email) {
        this.view = sView;
        this.authModel = authM;
        this.documentModel = docM;
        this.dashboardView = dView;
        this.dashboardController = dCtrl; // Simpan referensi
        this.currentUserEmail = email;

        initListeners();
        loadSharedDocumentsData();
    }

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        loadSharedDocumentsData(); // Muat ulang data jika user berubah
    }

    private void initListeners() {
        view.getBackToDashboardButton().addActionListener(e -> {
            view.getSharedFrame().setVisible(false);
            if (dashboardController != null) { // Gunakan referensi dashboardController
                dashboardController.navigateBackToDashboardAndRefresh();
            } else if (dashboardView != null) { // Fallback
                dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                dashboardView.getDashboardFrame().setVisible(true);
            }
        });

        view.getLogoutButton().addActionListener(e -> {
            view.getSharedFrame().setVisible(false);
            if (dashboardController != null) {
                 if (dashboardView != null && dashboardView.getLogoutButton() != null && dashboardView.getLogoutButton().getActionListeners().length > 0) {
                     dashboardView.getLogoutButton().doClick();
                 } else { System.exit(0); }
            } else if (dashboardView != null && dashboardView.getLogoutButton() != null && dashboardView.getLogoutButton().getActionListeners().length > 0) {
                dashboardView.getLogoutButton().doClick();
            } else {
                System.exit(0);
            }
        });

        JTable youSharedTable = view.getDocumentsYouSharedTable();
        if (youSharedTable != null) { // Tambahkan null check
            youSharedTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = youSharedTable.rowAtPoint(evt.getPoint());
                    int col = youSharedTable.columnAtPoint(evt.getPoint());
                    
                    // Pastikan kolom "Aksi" ada dan indeksnya benar
                    int aksiColIndex = -1;
                    try { aksiColIndex = youSharedTable.getColumnModel().getColumnIndex("Aksi"); }
                    catch (IllegalArgumentException ex) { /* Kolom tidak ada, abaikan */ return; }

                    if (row >= 0 && col == aksiColIndex) {
                        String actionCommand = (String) youSharedTable.getClientProperty("LAST_ACTION_COMMAND_SHARED_DOCS");
                        // Reset properti setelah dibaca untuk menghindari eksekusi berulang jika tidak di-set ulang oleh view
                        // youSharedTable.putClientProperty("LAST_ACTION_COMMAND_SHARED_DOCS", null); // Sebaiknya view yang clear

                        String docName = (String) youSharedTable.getValueAt(row, 0);
                        String sharedWithEmail = (String) youSharedTable.getValueAt(row, 1);

                        if (SharedDocumentView.ACTION_CMD_VIEW_PROFILE.equals(actionCommand)) {
                            handleViewUserProfile(sharedWithEmail);
                        } else if (SharedDocumentView.ACTION_CMD_DELETE_ACCESS.equals(actionCommand)) {
                            handleRevokeAccess(docName, sharedWithEmail);
                        }
                    }
                }
            });
        }


        JTable sharedWithYouTable = view.getDocumentsSharedWithYouTable();
        if (sharedWithYouTable != null) { // Tambahkan null check
            sharedWithYouTable.addMouseListener(new java.awt.event.MouseAdapter() {
                 public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = sharedWithYouTable.rowAtPoint(evt.getPoint());
                    int col = sharedWithYouTable.columnAtPoint(evt.getPoint());
                    
                    int aksiColIndex = -1;
                    try { aksiColIndex = sharedWithYouTable.getColumnModel().getColumnIndex("Aksi"); }
                    catch (IllegalArgumentException ex) { /* Kolom tidak ada, abaikan */ return; }

                    if (row >= 0 && col == aksiColIndex) {
                        String actionCommand = (String) sharedWithYouTable.getClientProperty("LAST_ACTION_COMMAND_SHARED_DOCS");
                        // sharedWithYouTable.putClientProperty("LAST_ACTION_COMMAND_SHARED_DOCS", null); // View yang clear

                        String docName = (String) sharedWithYouTable.getValueAt(row, 0);
                        String sharedByEmail = (String) sharedWithYouTable.getValueAt(row, 1);

                        if (SharedDocumentView.ACTION_CMD_VIEW_DOC_SHARED_TO_YOU.equals(actionCommand)) {
                            handleViewDocument(docName, sharedByEmail);
                        } else if (SharedDocumentView.ACTION_CMD_DOWNLOAD_DOC_SHARED_TO_YOU.equals(actionCommand)) {
                            handleDownloadDocument(docName, sharedByEmail);
                        } else if (SharedDocumentView.ACTION_CMD_UPDATE_DOC_SHARED_TO_YOU.equals(actionCommand)) {
                            handleUpdateSharedDocument(docName, sharedByEmail);
                        }
                    }
                }
            });
        }
    }

    public void loadSharedDocumentsData() {
        DefaultTableModel youSharedModel = view.getDocumentsYouSharedTableModel();
        DefaultTableModel sharedWithYouModel = view.getDocumentsSharedWithYouTableModel();
        
        if (youSharedModel == null || sharedWithYouModel == null) {
            System.err.println("SharedDocumentController: Table model null, tidak bisa memuat data.");
            return;
        }
        youSharedModel.setRowCount(0);
        sharedWithYouModel.setRowCount(0);

        Map<String, Document> ownedDocs = documentModel.getDocumentsOwnedBy(currentUserEmail);
        boolean hasSharedAnyDocs = false;
        if (ownedDocs != null) {
            for (Document doc : ownedDocs.values()) {
                Map<String, Document.AccessDetail> shares = documentModel.getSharesByDocument(currentUserEmail, doc.getName());
                if (shares != null && !shares.isEmpty()) {
                    hasSharedAnyDocs = true;
                    for (Map.Entry<String, Document.AccessDetail> shareEntry : shares.entrySet()) {
                        youSharedModel.addRow(new Object[]{
                            doc.getName(),
                            shareEntry.getKey(), 
                            shareEntry.getValue().timestampGiven, 
                            null // Placeholder untuk kolom Aksi (dirender oleh TableCellRenderer)
                        });
                    }
                }
            }
        }
        if (!hasSharedAnyDocs) {
             youSharedModel.addRow(new Object[]{"Anda belum membagikan dokumen apapun.", "", "", null});
        }

        Map<String, Document> docsSharedToUser = documentModel.getDocumentsSharedWithUser(currentUserEmail);
        if (docsSharedToUser == null || docsSharedToUser.isEmpty()) {
            sharedWithYouModel.addRow(new Object[]{"Tidak ada dokumen yang dibagikan kepada Anda.", "", "", null}); // Tambah null untuk Aksi
        } else {
            docsSharedToUser.values().forEach(doc -> {
                Document.AccessDetail accessDetail = doc.getAccessListWithTimestamp().get(currentUserEmail);
                String permissionsString = ""; 
                // Kolom Aksi akan dirender oleh View, data di sini bisa untuk logika internal jika perlu
                // Untuk sekarang, kolom 'Aksi' di model tabel tidak perlu diisi data spesifik di sini
                // jika View sudah mengaturnya. Tapi jika View mengambil string dari model untuk tombol,
                // maka string permissions perlu dipertimbangkan.

                // Contoh pengisian kolom ke-3 (sebelumnya permissionsString, sekarang bisa jadi timestamp atau tipe akses)
                String displayInfoCol3 = (accessDetail != null) ? accessDetail.accessType + " (sejak " + accessDetail.timestampGiven + ")" : "Info tidak tersedia";

                sharedWithYouModel.addRow(new Object[]{
                    doc.getName(), 
                    doc.getOwnerEmail(), 
                    displayInfoCol3, // Informasi tambahan
                    null // Placeholder untuk kolom Aksi
                });
            });
        }
    }
    
    private void handleViewUserProfile(String userEmailToShow) {
        if (userEmailToShow == null || userEmailToShow.isEmpty() || userEmailToShow.contains("belum membagikan")){ 
            // Jangan lakukan apa-apa jika email tidak valid (misal dari baris placeholder)
            return; 
        }
        view.getSharedFrame().setVisible(false);
        String usernameTarget = authModel.getUsernameByEmail(userEmailToShow);
        if (usernameTarget == null || usernameTarget.isEmpty()) usernameTarget = userEmailToShow.split("@")[0]; 
        
        String targetPicPath = authModel.getUserProfilePicturePath(userEmailToShow); 
        if (targetPicPath == null || targetPicPath.isEmpty() || !new File(targetPicPath).exists()) {
             targetPicPath = "placeholder/default_avatar.png";
        }

        if (targetProfileView == null) {
            targetProfileView = new ProfileView(usernameTarget, userEmailToShow);
            // Teruskan DashboardController untuk navigasi kembali yang benar
            targetProfileController = new ProfileController(targetProfileView, authModel, dashboardView, dashboardController, userEmailToShow);
        } else {
            targetProfileController.updateUserProfileData(userEmailToShow); 
        }
        // targetProfileView.loadProfilePicture(targetPicPath); // Biarkan ProfileController yang memuat ulang
        targetProfileView.getProfileFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        targetProfileView.setVisible(true); 
    }

    private void handleRevokeAccess(String documentName, String emailToRevoke) {
        if (documentName == null || documentName.contains("belum membagikan") || emailToRevoke == null || emailToRevoke.isEmpty()) {
            return; // Abaikan jika dari baris placeholder
        }
        int confirm = JOptionPane.showConfirmDialog(view.getSharedFrame(),
                "Cabut akses untuk " + emailToRevoke + " dari dokumen '" + documentName + "'?",
                "Konfirmasi Cabut Akses", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (documentModel.revokeAccess(documentName, currentUserEmail, emailToRevoke)) {
                JOptionPane.showMessageDialog(view.getSharedFrame(), "Akses berhasil dicabut.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSharedDocumentsData(); 
            } else {
                JOptionPane.showMessageDialog(view.getSharedFrame(), "Gagal mencabut akses.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewDocument(String docName, String sharedByEmail) {
        if (docName == null || docName.contains("Tidak ada dokumen")) return;

        Document doc = documentModel.getDocumentsSharedWithUser(currentUserEmail).get(docName);
        // Jika dokumen juga milik user (misal via owner access type), bisa juga dicari di ownedDocs
        if (doc == null) { 
             doc = documentModel.getDocumentsOwnedBy(currentUserEmail).get(docName);
        }
        // Fallback jika sangat perlu, cari di semua dokumen (termasuk recycle bin, mungkin tidak ideal)
        // if (doc == null) { 
        //     doc = documentModel.getAllDocumentsIncludingRecycled().get(docName); // Kunci di sini mungkin perlu penyesuaian
        // }

        if (doc != null && doc.getOwnerEmail().equals(sharedByEmail) && doc.getFilePath() != null && !doc.getFilePath().startsWith("placeholder/")) {
            try {
                File fileToOpen = new File(doc.getFilePath());
                if (fileToOpen.exists() && fileToOpen.isFile()) { 
                    if (Desktop.isDesktopSupported()) { // Cek dukungan Desktop API
                        Desktop.getDesktop().open(fileToOpen);
                    } else {
                         JOptionPane.showMessageDialog(view.getSharedFrame(), "Tidak dapat membuka file secara otomatis pada sistem ini.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(view.getSharedFrame(), "File tidak ditemukan di path: " + doc.getFilePath(), "Error Buka File", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(view.getSharedFrame(), "Gagal membuka file '" + docName + "': " + ex.getMessage(), "Error Buka File", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (UnsupportedOperationException uoe) {
                 JOptionPane.showMessageDialog(view.getSharedFrame(), "Operasi buka file tidak didukung pada sistem ini.", "Error Buka File", JOptionPane.ERROR_MESSAGE);
                 uoe.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(view.getSharedFrame(), "Path file tidak valid, placeholder, atau info pemilik tidak cocok untuk '" + docName + "'. Tidak bisa dilihat.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDownloadDocument(String docName, String sharedByEmail) {
        if (docName == null || docName.contains("Tidak ada dokumen")) return;

         Document doc = documentModel.getDocumentsSharedWithUser(currentUserEmail).get(docName);
         if (doc == null) { 
             doc = documentModel.getDocumentsOwnedBy(currentUserEmail).get(docName);
         }
         // if (doc == null) { // Fallback jika perlu
         //    doc = documentModel.getAllDocumentsIncludingRecycled().get(docName);  // Kunci di sini mungkin perlu penyesuaian
         // }
        if (doc != null && doc.getOwnerEmail().equals(sharedByEmail) && doc.getFilePath() != null && !doc.getFilePath().isEmpty() && !doc.getFilePath().startsWith("placeholder/")) {
            File sourceFile = new File(doc.getFilePath());
             if (!sourceFile.exists() || !sourceFile.isFile()) {
                 JOptionPane.showMessageDialog(view.getSharedFrame(), "File sumber tidak ditemukan di path: " + doc.getFilePath(), "Error Download", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Dokumen Sebagai");
            fileChooser.setSelectedFile(new File(doc.getName()));
            int userSelection = fileChooser.showSaveDialog(view.getSharedFrame());

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSaveTo = fileChooser.getSelectedFile();
                String originalName = doc.getName();
                String originalExtension = "";
                int dotIndex = originalName.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < originalName.length() - 1) {
                    originalExtension = originalName.substring(dotIndex);
                }
                if (!fileToSaveTo.getName().toLowerCase().endsWith(originalExtension.toLowerCase()) && !originalExtension.isEmpty()) {
                    fileToSaveTo = new File(fileToSaveTo.getParentFile(), fileToSaveTo.getName() + originalExtension);
                }

                try {
                    Files.copy(sourceFile.toPath(), fileToSaveTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(view.getSharedFrame(), "Dokumen '" + docName + "' berhasil di-download.", "Download Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view.getSharedFrame(), "Gagal men-download file: " + ex.getMessage(), "Error Download", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(view.getSharedFrame(), "Path file tidak valid, placeholder, atau info pemilik tidak cocok untuk '" + docName + "'. Tidak bisa di-download.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleUpdateSharedDocument(String docName, String sharedByEmail) {
        if (docName == null || docName.contains("Tidak ada dokumen")) return;
        
        Document docToUpdate = documentModel.getDocumentsSharedWithUser(currentUserEmail).get(docName);
        // Jika tidak ditemukan di shared, mungkin itu adalah dokumen milik sendiri yang muncul di daftar "shared with you" karena logika tertentu (seharusnya tidak)
        // Atau jika user adalah pemilik, dia tetap bisa update.
        if (docToUpdate == null && currentUserEmail.equals(sharedByEmail)) { 
             docToUpdate = documentModel.getDocumentsOwnedBy(currentUserEmail).get(docName);
        }
        // if (docToUpdate == null) { // Fallback jika sangat perlu
        //    docToUpdate = documentModel.getAllDocumentsIncludingRecycled().get(docName); // Kunci di sini mungkin perlu penyesuaian
        // }

        if (docToUpdate != null && docToUpdate.getOwnerEmail().equals(sharedByEmail)) {
            Document.AccessDetail access = docToUpdate.getAccessListWithTimestamp().get(currentUserEmail);
            // Pengguna harus memiliki akses 'read-write' atau menjadi 'owner' untuk update
            boolean canUpdate = (access != null && (access.accessType.equals("read-write") || access.accessType.equals("owner")));
            
            if (canUpdate) {
                view.getSharedFrame().setVisible(false);
                String initialContent = "Gagal memuat konten atau format tidak didukung.";
                boolean isEditable = false;
                
                if (docToUpdate.getFilePath() != null && !docToUpdate.getFilePath().startsWith("placeholder/")) {
                    String filePath = docToUpdate.getFilePath();
                    File file = new File(filePath);
                     if (!file.exists() || !file.isFile()){
                        initialContent = "File tidak ditemukan di path: " + filePath;
                        isEditable = false;
                    } else if (docToUpdate.getName().toLowerCase().endsWith(".txt")) {
                        try { 
                            initialContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8); 
                            isEditable = true; 
                        } catch (IOException ex) { 
                            initialContent = "Gagal baca file .txt: " + ex.getMessage(); 
                            ex.printStackTrace();
                        }
                    } else if (docToUpdate.getName().toLowerCase().endsWith(".docx")) {
                        if (file.length() == 0) {
                             initialContent = "File .docx kosong (0 bytes).";
                             isEditable = true;
                        } else {
                            try (FileInputStream fis = new FileInputStream(filePath);
                                 XWPFDocument docx = new XWPFDocument(fis);
                                 XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                                initialContent = extractor.getText();
                                if(initialContent.isEmpty() && docx.getParagraphs().size() > 0) initialContent = "(Dokumen .docx non-teks/kosong)";
                                else if(initialContent.isEmpty()) initialContent = "(Dokumen .docx kosong)";
                                isEditable = true;
                            } catch (ZipException ze) {
                                initialContent = String.format("Gagal memuat .docx '%s': File ZIP/DOCX tidak valid.\nDetail: %s", docToUpdate.getName(), ze.getMessage());
                                isEditable = false; ze.printStackTrace();
                            } catch (InvalidFormatException ife) {
                                initialContent = String.format("Gagal memuat .docx '%s': Format file tidak valid.\nDetail: %s", docToUpdate.getName(), ife.getMessage());
                                isEditable = false; ife.printStackTrace();
                            } catch (OpenXML4JException | IOException e_poi_io) {
                                initialContent = "Gagal baca .docx: " + e_poi_io.getMessage(); 
                                isEditable = false; e_poi_io.printStackTrace();
                            } catch (Exception e_general) {
                                initialContent = "Terjadi kesalahan tak terduga saat memuat .docx: " + e_general.getMessage();
                                isEditable = false; e_general.printStackTrace();
                            }
                        }
                    } else {
                        initialContent = "Format file ("+ docToUpdate.getName() +") tidak didukung untuk edit konten.";
                    }
                } else {
                    initialContent = "Dokumen placeholder, tidak bisa diedit kontennya.";
                    isEditable = false;
                }

                if (updateSharedDocView == null) {
                    updateSharedDocView = new UpdateDocumentView(docToUpdate.getName(), docToUpdate.getFilePath(), initialContent, isEditable);
                    // Untuk kembali ke SharedDocumentView, UpdateDocumentController perlu dimodifikasi untuk menerima
                    // parent view yang lebih generik atau callback. Untuk sekarang, kita set parent DocumentView ke null.
                    // Atau, UpdateDocumentController bisa memiliki logika untuk kembali ke SharedDocumentView jika parentDocumentView null.
                    updateSharedDocController = new UpdateDocumentController(updateSharedDocView, authModel, documentModel, docToUpdate, null /*parent DocumentView*/, currentUserEmail);
                    // TODO: Pastikan UpdateDocumentController bisa kembali ke SharedDocumentView
                    // Salah satu cara adalah dengan membuat UpdateDocumentController menerima Object sebagai parent view
                    // dan melakukan instanceof check, atau menggunakan interface callback.
                } else {
                    updateSharedDocView.setDocumentData(docToUpdate.getName(), docToUpdate.getFilePath(), initialContent, isEditable);
                    updateSharedDocController.setDocumentToUpdate(docToUpdate, currentUserEmail);
                }
                updateSharedDocView.getUpdateFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                updateSharedDocView.setVisible(true); 

            } else {
                 JOptionPane.showMessageDialog(view.getSharedFrame(), "Anda tidak memiliki izin ('read-write' atau 'owner') untuk mengupdate dokumen '" + docName + "'.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view.getSharedFrame(), "Dokumen '" + docName + "' tidak ditemukan atau info pemilik tidak cocok.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        loadSharedDocumentsData();
    }

    // Hapus placeholder jika tidak digunakan
    // private Object getDashboardControllerInstance() { return this.dashboardController; } 
}