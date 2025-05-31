// File: Controller/DocumentController.java
package Controller;

import View.DocumentView;
import View.DashboardView;
import View.UpdateDocumentView;
import View.SharedDocumentView; 
import Controller.UpdateDocumentController; 
// import Controller.SharedDocumentController; // Sudah diimpor di atas jika diperlukan

import Model.AuthModel;     
import Model.Document;
import Model.DocumentModel; 

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.awt.Frame;

// Import Apache POI
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.util.zip.ZipException; // Ditambahkan

public class DocumentController {
    private DocumentView documentView;
    private DashboardView dashboardView; // Untuk navigasi kembali
    private DashboardController dashboardController; // Untuk memanggil refresh
    private AuthModel authModel;         
    private DocumentModel documentModel; 
    private String currentUserEmail;

    private UpdateDocumentView updateDocumentFileView; 
    private UpdateDocumentController updateDocumentFileController; 

    private SharedDocumentView sharedDocumentView; 
    private SharedDocumentController sharedDocumentController; // Deklarasi


    // Konstruktor diubah untuk menerima DashboardController juga
    public DocumentController(DocumentView dView, DashboardView dashV, AuthModel authM, DocumentModel docM, DashboardController dashCtrl) {
        this.documentView = dView;
        this.dashboardView = dashV; // Simpan referensi ke DashboardView
        this.authModel = authM;
        this.documentModel = docM; 
        this.dashboardController = dashCtrl; // Simpan referensi ke DashboardController
        initDocumentViewListeners();
    }

    public void setCurrentUser(String email) {
        this.currentUserEmail = email;
        if (this.currentUserEmail == null || this.currentUserEmail.trim().isEmpty()) {
            System.err.println("PERINGATAN: currentUserEmail tidak valid atau belum di-set di DocumentController.");
            // Mungkin bersihkan tabel jika pengguna tidak valid
            documentView.clearDocumentTable();
        }
        loadDocuments();
    }

    private void initDocumentViewListeners() {
        if (documentView.getBackButton() != null) {
            documentView.getBackButton().addActionListener(e -> {
                documentView.getDocumentFrame().setVisible(false);
                if (dashboardController != null) { // Gunakan referensi dashboardController
                    dashboardController.navigateBackToDashboardAndRefresh();
                } else if (dashboardView != null) { // Fallback jika dashboardController null
                    dashboardView.getDashboardFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                    dashboardView.getDashboardFrame().setVisible(true);
                     // Idealnya, panggil metode refresh di DashboardController jika ada.
                } else {
                    System.err.println("Error: DashboardView/Controller tidak tersedia untuk navigasi kembali dari DocumentView.");
                }
            });
        } // ... (sisa listener tetap sama) ...

        if (documentView.getAddButton() != null) {
            documentView.getAddButton().addActionListener(e -> handleAddDocument());
        }
        if (documentView.getUpdateButton() != null) {
            documentView.getUpdateButton().addActionListener(e -> handleUpdateDocumentAction());
        }
        if (documentView.getDeleteButton() != null) {
            documentView.getDeleteButton().addActionListener(e -> handleDeleteDocumentAction());
        }
        if (documentView.getAccessButton() != null) {
            documentView.getAccessButton().addActionListener(e -> handleAccessAction());
        }
        if (documentView.getDownloadButton() != null) {
            documentView.getDownloadButton().addActionListener(e -> handleDownloadAction());
        }
        if (documentView.getLogoutButton() != null) {
            documentView.getLogoutButton().addActionListener(e -> handleLogoutAction());
        }
        if (documentView.getSharedDocumentsButtonSidebar() != null) {
            documentView.getSharedDocumentsButtonSidebar().addActionListener(e -> navigateToSharedDocumentsView());
        }
    }

    private void handleAddDocument() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Tidak dapat menambahkan dokumen, pengguna tidak teridentifikasi.", "Error Pengguna", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih file dokumen yang akan ditambahkan");
        fileChooser.setMultiSelectionEnabled(false);
        int userSelection = fileChooser.showOpenDialog(documentView.getDocumentFrame());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();
            String filePath = selectedFile.getAbsolutePath().replace("\\", "/"); // Simpan path absolut, normalisasi separator
            String fileType = "";
            try {
                fileType = Files.probeContentType(selectedFile.toPath());
                if(fileType == null) { 
                    int lastDot = fileName.lastIndexOf('.');
                    if (lastDot > 0 && lastDot < fileName.length() - 1) {
                        String ext = fileName.substring(lastDot + 1).toLowerCase();
                        if (ext.equals("pdf")) fileType = "application/pdf";
                        else if (ext.equals("docx")) fileType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        else if (ext.equals("txt")) fileType = "text/plain";
                        // Tambahkan ekstensi lain jika perlu
                        else fileType = "application/octet-stream";
                    } else {
                        fileType = "application/octet-stream";
                    }
                }
            } catch (IOException ex) {
                System.err.println("Gagal mendapatkan tipe MIME: " + ex.getMessage());
                fileType = "application/octet-stream"; // Default jika gagal
            }
            int fileSize = (int) selectedFile.length();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            
            int ownerUserId = -1;
            try { 
                AuthModel.User owner = authModel.getUserByEmailFromDb(currentUserEmail); 
                if (owner != null) ownerUserId = owner.getUserId();
                else { // Jika user tidak ditemukan di DB (seharusnya tidak terjadi jika sudah login)
                     JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Data pengguna saat ini tidak ditemukan di database.", "Error Pengguna", JOptionPane.ERROR_MESSAGE);
                     return;
                }
            } catch (Exception exSQL) { 
                JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Gagal mendapatkan ID pengguna: " + exSQL.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                exSQL.printStackTrace();
                return;
            }
            if(ownerUserId == -1) { // Seharusnya sudah ditangani di atas
                 JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Pengguna saat ini tidak valid untuk memiliki dokumen.", "Error Pengguna", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (documentModel.addDocumentToDb(fileName, ownerUserId, timestamp, filePath, fileType, fileSize)) { 
                loadDocuments();
                JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Dokumen '" + fileName + "' berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Gagal menambahkan dokumen. Nama mungkin sudah ada untuk Anda, atau terjadi kesalahan lain.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUpdateDocumentAction() { 
        int selectedRow = documentView.getTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Pilih dokumen yang akan diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Asumsi kolom 0 adalah nama dokumen
        String docName = (String) documentView.getTableModel().getValueAt(selectedRow, 0);
        // Asumsi kolom 1 adalah email pemilik
        String ownerEmailFromTable = (String) documentView.getTableModel().getValueAt(selectedRow, 1);

        // Validasi apakah pengguna saat ini adalah pemilik dokumen
        if (!currentUserEmail.equals(ownerEmailFromTable)) {
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Anda hanya bisa mengupdate dokumen milik Anda.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Document documentToUpdate = documentModel.findDocumentByNameAndOwner(docName, currentUserEmail); 

        if (documentToUpdate != null) {
            navigateToUpdateDocumentFileView(documentToUpdate); 
        } else {
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Detail untuk dokumen '" + docName + "' tidak ditemukan atau Anda bukan pemiliknya.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteDocumentAction() { 
        int selectedRow = documentView.getTable().getSelectedRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Pilih dokumen yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        String docName = (String) documentView.getTableModel().getValueAt(selectedRow, 0);
        String ownerEmailFromTable = (String) documentView.getTableModel().getValueAt(selectedRow, 1);

        if (!currentUserEmail.equals(ownerEmailFromTable)) {
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Anda hanya bisa menghapus dokumen milik Anda.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(documentView.getDocumentFrame(),
                "Pindahkan dokumen '" + docName + "' ke Recycle Bin?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (documentModel.removeDocument(docName, currentUserEmail)) { 
                loadDocuments();
                JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Dokumen '" + docName + "' telah dipindahkan ke Recycle Bin.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else { 
                JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Gagal memindahkan '" + docName + "' ke Recycle Bin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAccessAction() { 
        int selectedRow = documentView.getTable().getSelectedRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Pilih dokumen untuk memberi akses.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        String docName = (String) documentView.getTableModel().getValueAt(selectedRow, 0);
        String ownerEmailFromTable = (String) documentView.getTableModel().getValueAt(selectedRow, 1);

        if (!currentUserEmail.equals(ownerEmailFromTable)) { 
             JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Anda hanya bisa memberi akses pada dokumen yang Anda miliki.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return; 
        }

        Document doc = documentModel.findDocumentByNameAndOwner(docName, currentUserEmail);
        if (doc == null) { // Seharusnya tidak terjadi jika validasi di atas lolos
             JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Dokumen tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailToGrant = JOptionPane.showInputDialog(documentView.getDocumentFrame(), "Masukkan email pengguna yang akan diberi akses:", "Beri Akses: " + docName, JOptionPane.PLAIN_MESSAGE);
        if (emailToGrant != null && !emailToGrant.trim().isEmpty()) {
            emailToGrant = emailToGrant.trim();
            if (emailToGrant.equals(currentUserEmail)) { 
                JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Anda tidak bisa memberi akses tambahan pada diri sendiri.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return; 
            }
            // Cek apakah pengguna yang akan diberi akses terdaftar
            if (authModel.getUserByEmailFromDb(emailToGrant) == null) {
                 JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Email '" + emailToGrant + "' tidak terdaftar dalam sistem.", "Error Pengguna", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] accessTypes = {"read", "read-write"}; // Tipe akses yang bisa dipilih
            String accessType = (String) JOptionPane.showInputDialog(documentView.getDocumentFrame(), "Pilih tipe akses untuk " + emailToGrant + ":", 
                "Tipe Akses", JOptionPane.QUESTION_MESSAGE, null, accessTypes, accessTypes[0]);
            
            if (accessType != null) {
                if (documentModel.grantAccess(docName, currentUserEmail, emailToGrant, accessType)) { 
                    JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Akses '" + accessType + "' untuk '" + docName + "' berhasil diberikan kepada " + emailToGrant + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    // Mungkin refresh tabel atau navigasi ke tampilan dokumen bersama
                    // navigateToSharedDocumentsView(); // Uncomment jika ingin langsung pindah
                } else { 
                    JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Gagal memberi akses. Pengguna mungkin sudah memiliki akses tersebut atau terjadi kesalahan lain.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDownloadAction() { 
        int selectedRow = documentView.getTable().getSelectedRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Pilih dokumen yang akan di-download.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        String docName = (String) documentView.getTableModel().getValueAt(selectedRow, 0);
        String ownerEmailFromTable = (String) documentView.getTableModel().getValueAt(selectedRow, 1);

        // Di sini kita butuh Document object untuk mendapatkan file path.
        // Kita bisa ambil dari active documents di model, atau jika ini shared doc, dari shared list.
        // Untuk simplicity, asumsikan DocumentController hanya menangani dokumen dari getActiveDocuments (milik sendiri atau shared ke dia)
        Document docToDownload = documentModel.findDocumentByNameAndOwner(docName, ownerEmailFromTable); // Coba cari sebagai pemilik
        if (docToDownload == null) { // Jika bukan pemilik, coba cari di dokumen yang dishare ke currentUser
            Map<String, Document> sharedToMe = documentModel.getDocumentsSharedWithUser(currentUserEmail);
            if (sharedToMe.containsKey(docName) && sharedToMe.get(docName).getOwnerEmail().equals(ownerEmailFromTable)) {
                 docToDownload = sharedToMe.get(docName);
            }
        }
        
        if (docToDownload == null) {
             JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Dokumen '" + docName + "' tidak ditemukan atau tidak dapat diakses untuk di-download.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String filePath = docToDownload.getFilePath();
        if (filePath != null && !filePath.isEmpty() && !filePath.startsWith("placeholder/")) {
            File sourceFile = new File(filePath);
            if (!sourceFile.exists() || !sourceFile.isFile()) {
                 JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "File sumber tidak ditemukan di path: " + filePath, "Error Download", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Sebagai");
            fileChooser.setSelectedFile(new File(docToDownload.getName()));
            int userSelection = fileChooser.showSaveDialog(documentView.getDocumentFrame());
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSaveTo = fileChooser.getSelectedFile();
                // Ambil ekstensi asli dan pastikan file simpanan memilikinya
                String originalName = docToDownload.getName();
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
                    JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Dokumen '" + docName + "' berhasil di-download ke:\n" + fileToSaveTo.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioex) { 
                    JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Gagal download: " + ioex.getMessage(), "Error IO", JOptionPane.ERROR_MESSAGE);
                    ioex.printStackTrace(); 
                } catch (Exception ex) { 
                    JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Error download tidak diketahui: " + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); 
                }
            }
        } else { 
            JOptionPane.showMessageDialog(documentView.getDocumentFrame(), "Path file tidak valid atau placeholder. Tidak bisa di-download.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogoutAction() { 
        // Logika logout yang lebih baik adalah memanggil metode logout di DashboardController atau AuthController
        if (documentView != null && documentView.getDocumentFrame() != null) documentView.getDocumentFrame().dispose();
        if (updateDocumentFileView != null && updateDocumentFileView.getUpdateFrame() != null) { 
            updateDocumentFileView.getUpdateFrame().dispose(); 
        }
        if (sharedDocumentView != null && sharedDocumentView.getSharedFrame() != null) { 
            sharedDocumentView.getSharedFrame().dispose();
        }
        
        // Panggil metode logout di DashboardController jika tersedia
        if (dashboardController != null) {
            // Asumsi DashboardController memiliki metode publik untuk menangani logout
            // dashboardController.handleLogout(); // Jika ada metode seperti ini
            // Atau trigger tombol logout di DashboardView jika itu cara kerjanya
            if (dashboardView != null && dashboardView.getLogoutButton() != null && dashboardView.getLogoutButton().getActionListeners().length > 0) {
                 dashboardView.getLogoutButton().doClick();
            } else {
                 System.err.println("Tidak bisa trigger logout dari DocumentController. Fallback ke System.exit.");
                 System.exit(0); // Darurat jika tidak bisa logout dengan bersih
            }
        } else if (dashboardView != null && dashboardView.getLogoutButton() != null && dashboardView.getLogoutButton().getActionListeners().length > 0) {
             dashboardView.getLogoutButton().doClick(); // Jika hanya punya view
        } else { 
            System.err.println("Tidak ada referensi ke Dashboard untuk logout dari DocumentController. Fallback ke System.exit.");
            System.exit(0); 
        }
    }

    private void navigateToUpdateDocumentFileView(Document documentToUpdate) {
        documentView.getDocumentFrame().setVisible(false);
        String initialContent = "Konten tidak dapat dimuat atau format tidak didukung.";
        boolean isContentEditable = false;

        if (documentToUpdate.getFilePath() != null && !documentToUpdate.getFilePath().startsWith("placeholder/")) {
            String filePath = documentToUpdate.getFilePath();
            String fileNameLower = documentToUpdate.getName().toLowerCase();
            File file = new File(filePath);

            if (!file.exists() || !file.isFile()){
                initialContent = "File tidak ditemukan di path: " + filePath;
                isContentEditable = false;
            } else if (fileNameLower.endsWith(".txt")) {
                try {
                    initialContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
                    isContentEditable = true;
                } catch (IOException ex) { 
                    initialContent = "Gagal membaca file .txt: " + ex.getMessage(); 
                    ex.printStackTrace();
                }
            } else if (fileNameLower.endsWith(".docx")) {
                if (file.length() == 0) {
                     initialContent = "File .docx kosong (0 bytes).";
                     isContentEditable = true; // Boleh diedit untuk diisi
                } else {
                    try (FileInputStream fis = new FileInputStream(filePath);
                         XWPFDocument docx = new XWPFDocument(fis); // Potensi ZipException
                         XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                        initialContent = extractor.getText();
                        if(initialContent.isEmpty() && docx.getParagraphs().size() > 0 && docx.getBodyElements().size() > 0) {
                            initialContent = "(Dokumen .docx mungkin berisi gambar atau objek lain, bukan hanya teks, atau teksnya kosong)";
                        } else if(initialContent.isEmpty()) {
                            initialContent = "(Dokumen .docx ini kosong)";
                        }
                        isContentEditable = true; 
                    } catch (ZipException ze) {
                        initialContent = String.format("Gagal memuat .docx '%s': File bukan format ZIP/DOCX yang valid atau rusak.\nDetail: %s", documentToUpdate.getName(), ze.getMessage());
                        isContentEditable = false; 
                        ze.printStackTrace();
                    } catch (InvalidFormatException ife) {
                        initialContent = String.format("Gagal memuat .docx '%s': Format file tidak valid.\nDetail: %s", documentToUpdate.getName(), ife.getMessage());
                        isContentEditable = false;
                        ife.printStackTrace();
                    } catch (OpenXML4JException | IOException e_poi_io) { 
                        initialContent = "Gagal membaca file .docx: " + e_poi_io.getMessage(); 
                        isContentEditable = false;
                        e_poi_io.printStackTrace(); 
                    } catch (Exception e_general) {
                        initialContent = "Terjadi kesalahan tak terduga saat memuat .docx: " + e_general.getMessage();
                        isContentEditable = false;
                        e_general.printStackTrace();
                    }
                }
            } else { 
                initialContent = "Format file ("+ documentToUpdate.getName() +") tidak didukung untuk edit konten."; 
            }
        } else { 
            initialContent = "Dokumen placeholder, tidak bisa diedit kontennya."; 
            isContentEditable = false;
        }

        if (updateDocumentFileView == null) {
            updateDocumentFileView = new UpdateDocumentView(documentToUpdate.getName(), documentToUpdate.getFilePath(), initialContent, isContentEditable);
            // Teruskan DocumentView ini sebagai parent untuk kembali
            updateDocumentFileController = new UpdateDocumentController(updateDocumentFileView, authModel, documentModel, documentToUpdate, documentView, currentUserEmail);
        } else {
            updateDocumentFileView.setDocumentData(documentToUpdate.getName(), documentToUpdate.getFilePath(), initialContent, isContentEditable);
            updateDocumentFileController.setDocumentToUpdate(documentToUpdate, currentUserEmail);
        }
        if (updateDocumentFileView.getUpdateFrame() != null) { 
            updateDocumentFileView.getUpdateFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
            updateDocumentFileView.getUpdateFrame().setVisible(true);
        } else {
             System.err.println("Error: Gagal menampilkan UpdateDocumentFileView karena frame-nya null.");
        }
    }
    
    private void navigateToSharedDocumentsView() {
        documentView.getDocumentFrame().setVisible(false);
        String username = authModel.getUsernameByEmail(currentUserEmail);
        if (username == null) username = currentUserEmail.split("@")[0]; 

        if (sharedDocumentView == null) {
            sharedDocumentView = new SharedDocumentView(username);
            // Teruskan DashboardController untuk navigasi kembali dan potensi refresh
            sharedDocumentController = new SharedDocumentController(sharedDocumentView, authModel, documentModel, dashboardView, dashboardController, currentUserEmail);
        } else {
            sharedDocumentController.setCurrentUserEmail(currentUserEmail); // Update user jika berubah
            sharedDocumentController.refreshData(); 
        }
        sharedDocumentView.getSharedFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        sharedDocumentView.setVisible(true); 
    }

    public void loadDocuments() {
        if (documentView == null || documentModel == null) { 
            System.err.println("DocumentController: View atau Model null, tidak bisa memuat dokumen.");
            return; 
        } 
        documentView.clearDocumentTable();
        Map<String, Document> activeDocs = documentModel.getActiveDocuments(); 
        if (activeDocs != null) {
            for (Document doc : activeDocs.values()) {
                // Hanya tampilkan dokumen milik currentUser atau yang di-share ke dia
                boolean ownedByUser = doc.getOwnerEmail().equals(currentUserEmail);
                boolean sharedToUser = doc.getAccessListWithTimestamp().containsKey(currentUserEmail) && 
                                       !doc.getAccessListWithTimestamp().get(currentUserEmail).accessType.equals("owner");
                
                if (ownedByUser || sharedToUser) {
                    documentView.addDocumentToTable(doc.getName(), doc.getOwnerEmail(), doc.getTimestamp(), doc.getFilePath());
                }
            }
        }
    }
    
    // Hapus placeholder getDashboardControllerInstance() jika tidak digunakan atau implementasikan dengan benar
    // private Object getDashboardControllerInstance() { return null; } 
}