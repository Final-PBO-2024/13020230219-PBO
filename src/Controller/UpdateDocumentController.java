package Controller;

import Model.AuthModel;
import Model.Document;
import Model.DocumentModel;
import View.DocumentView;
import View.UpdateDocumentView;

import javax.swing.*;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

// Import Apache POI
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage; // Lebih baik untuk membuka package
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.zip.ZipException; // Penting untuk menangani error ini

public class UpdateDocumentController {
    private UpdateDocumentView view;
    private AuthModel authModel;
    private DocumentModel documentModel;
    private Document documentOrigin; // Dokumen asli yang akan diupdate
    private DocumentView parentDocumentView;
    private String currentUserEmail;

    public UpdateDocumentController(UpdateDocumentView upView, AuthModel authM, DocumentModel docM,
                                    Document docToUpdate, DocumentView parentView, String userEmail) {
        this.view = upView;
        this.authModel = authM;
        this.documentModel = docM;
        this.documentOrigin = docToUpdate;
        this.parentDocumentView = parentView;
        this.currentUserEmail = userEmail;

        initListeners();

        // Memuat data dan konten dokumen awal ke dalam view
        if (this.documentOrigin != null) {
            loadInitialDocumentContent(this.documentOrigin);
            view.getUpdateFrame().setTitle("Edit Dokumen: " + this.documentOrigin.getName());
        } else {
            JOptionPane.showMessageDialog(view.getUpdateFrame(),
                    "Tidak ada data dokumen yang valid untuk ditampilkan.",
                    "Error Data Dokumen", JOptionPane.ERROR_MESSAGE);
            view.getFileNameField().setText("");
            view.getFileContentArea().setText("");
            view.getFileContentArea().setEditable(false);
        }
    }

    // Dipanggil jika instance controller ini digunakan kembali dengan dokumen baru
    public void setDocumentToUpdate(Document doc, String userEmail) {
        this.documentOrigin = doc;
        this.currentUserEmail = userEmail;
        if (this.documentOrigin != null) {
            loadInitialDocumentContent(this.documentOrigin);
            view.getUpdateFrame().setTitle("Edit Dokumen: " + this.documentOrigin.getName());
        } else {
            // Handle kasus dokumen null jika diperlukan
            view.getFileNameField().setText("");
            view.getFileContentArea().setText("Data dokumen tidak tersedia.");
            view.getFileContentArea().setEditable(false);
            view.getUpdateFrame().setTitle("Edit Dokumen: Tidak Ada Dokumen");
        }
    }

    /**
     * Memuat nama file dan konten dari objek Document ke dalam field di UpdateDocumentView.
     * Metode ini juga menangani potensi ZipException saat memuat file .docx.
     *
     * @param doc Dokumen yang akan dimuat kontennya.
     */
    private void loadInitialDocumentContent(Document doc) {
        if (doc == null || view == null) {
            System.err.println("Dokumen atau view null, tidak dapat memuat konten.");
            if (view != null) {
                view.getFileNameField().setText("");
                view.getFileContentArea().setText("Gagal memuat data dokumen.");
                view.getFileContentArea().setEditable(false);
            }
            return;
        }

        String fileName = doc.getName();
        String filePath = doc.getFilePath();
        view.getFileNameField().setText(fileName);
        view.getFileContentArea().setText(""); // Kosongkan dulu

        if (filePath == null || filePath.trim().isEmpty() || filePath.startsWith("placeholder/")) {
            view.getFileContentArea().setText("Konten tidak dapat ditampilkan (path tidak valid atau placeholder).");
            view.getFileContentArea().setEditable(false); // Tidak bisa diedit jika hanya placeholder
            return;
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            view.getFileContentArea().setText("File tidak ditemukan di path: " + filePath);
            view.getFileContentArea().setEditable(false);
            return;
        }
        
        // Tentukan apakah konten bisa diedit berdasarkan tipe file
        boolean isEditable = false;

        try {
            if (fileName.toLowerCase().endsWith(".txt")) {
                // Baca sebagai file teks biasa
                String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
                view.getFileContentArea().setText(content);
                isEditable = true;
            } else if (fileName.toLowerCase().endsWith(".docx")) {
                // Baca sebagai file .docx menggunakan Apache POI
                // Ini adalah tempat di mana ZipException kemungkinan besar terjadi jika file .docx rusak
                if (file.length() == 0) {
                     view.getFileContentArea().setText("File .docx kosong (0 bytes). Konten tidak dapat diekstrak.");
                     isEditable = true; // Biarkan user mengisi konten baru jika file asli kosong
                } else {
                    // Gunakan try-with-resources untuk memastikan stream ditutup
                    try (FileInputStream fis = new FileInputStream(file);
                         XWPFDocument document = new XWPFDocument(fis); // ZipException bisa terjadi di sini
                         XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                        
                        String textContent = extractor.getText();
                        view.getFileContentArea().setText(textContent);
                        isEditable = true; // Konten .docx (teks) bisa diedit di JTextArea
                    }
                }
            } else {
                // Tipe file lain tidak didukung untuk pengeditan konten langsung di JTextArea
                view.getFileContentArea().setText("Pratinjau/edit konten tidak didukung untuk tipe file ini (" + fileName + ").\nAnda masih bisa mengunduh dan mengganti nama file.");
                isEditable = false;
            }
        } catch (ZipException ze) {
            String errorMessage = String.format(
                "Gagal memuat file '%s': File bukan format ZIP/DOCX yang valid atau rusak.\nPastikan file .docx tidak korup.\nDetail: %s",
                fileName, ze.getMessage()
            );
            JOptionPane.showMessageDialog(view.getUpdateFrame(), errorMessage, "Error Memuat DOCX (ZipException)", JOptionPane.ERROR_MESSAGE);
            view.getFileContentArea().setText(errorMessage); // Tampilkan pesan error di area teks
            isEditable = false; // Tidak bisa diedit jika gagal load
            ze.printStackTrace();
        } catch (InvalidFormatException ife) {
            String errorMessage = String.format(
                "Gagal memuat file '%s': Format file .docx tidak valid.\nDetail: %s",
                fileName, ife.getMessage()
            );
            JOptionPane.showMessageDialog(view.getUpdateFrame(), errorMessage, "Error Memuat DOCX (Format Tidak Valid)", JOptionPane.ERROR_MESSAGE);
            view.getFileContentArea().setText(errorMessage);
            isEditable = false;
            ife.printStackTrace();
        } catch (OpenXML4JException | IOException ex) {
            String errorMessage = String.format(
                "Gagal membaca konten file '%s'.\nDetail: %s",
                fileName, ex.getMessage()
            );
            JOptionPane.showMessageDialog(view.getUpdateFrame(), errorMessage, "Error Membaca File", JOptionPane.ERROR_MESSAGE);
            view.getFileContentArea().setText(errorMessage);
            isEditable = false;
            ex.printStackTrace();
        } catch (Exception e) { // Menangkap exception umum lainnya
             String errorMessage = String.format(
                "Terjadi kesalahan tak terduga saat memuat file '%s'.\nDetail: %s",
                fileName, e.getMessage()
             );
            JOptionPane.showMessageDialog(view.getUpdateFrame(), errorMessage, "Error Umum", JOptionPane.ERROR_MESSAGE);
            view.getFileContentArea().setText(errorMessage);
            isEditable = false;
            e.printStackTrace();
        }
        
        view.getFileContentArea().setEditable(isEditable);
        view.getFileContentArea().setCaretPosition(0); // Pindahkan kursor ke awal
    }


    private void initListeners() {
        view.getBackButton().addActionListener(e -> {
            if (view.getUpdateFrame() != null) view.getUpdateFrame().dispose();
            if (parentDocumentView != null && parentDocumentView.getDocumentFrame() != null) {
                parentDocumentView.getDocumentFrame().setExtendedState(Frame.MAXIMIZED_BOTH); // Atau kondisi sebelumnya
                parentDocumentView.getDocumentFrame().setVisible(true);
                // Refresh tabel di DocumentView jika perlu
                Object parentController = getParentDocumentControllerInstance();
                if (parentController instanceof DocumentController) {
                    ((DocumentController) parentController).loadDocuments();
                }
            } else {
                System.out.println("UpdateDocumentView ditutup, tidak ada parent DocumentView spesifik.");
            }
        });

        view.getSaveButton().addActionListener(e -> handleSaveDocument());
        view.getDownloadButton().addActionListener(e -> handleDownloadDocument());
    }

    private void handleSaveDocument() {
        if (documentOrigin == null) {
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "Tidak ada dokumen yang valid untuk disimpan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "Sesi pengguna tidak valid. Tidak dapat menyimpan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String oldName = documentOrigin.getName();
        String newNameFromField = view.getFileNameField().getText().trim();
        String newContentFromTextArea = view.getFileContentArea().getText();

        if (newNameFromField.isEmpty()) {
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "Nama file tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            view.getFileNameField().requestFocus();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        // Path file fisik yang akan diupdate. Penting untuk memeriksa validitasnya.
        String filePathToUpdateOrCheck = documentOrigin.getFilePath(); 

        String contentForModelUpdate = null;
        boolean isContentActuallyEdited = view.getFileContentArea().isEditable(); // Cek apakah area teks memang untuk diedit

        // Hanya proses penyimpanan ke file fisik jika kontennya bisa diedit dan path valid
        if (isContentActuallyEdited && filePathToUpdateOrCheck != null && !filePathToUpdateOrCheck.startsWith("placeholder/")) {
            File physicalFile = new File(filePathToUpdateOrCheck);
            // Pastikan direktori parent ada jika file belum ada (meskipun ini untuk update, bisa jadi path berubah)
            if (physicalFile.getParentFile() != null && !physicalFile.getParentFile().exists()) {
                physicalFile.getParentFile().mkdirs();
            }

            String originalFileNameLower = documentOrigin.getName().toLowerCase(); // Gunakan nama asli dokumen untuk cek tipe

            try {
                if (originalFileNameLower.endsWith(".txt")) {
                    Files.write(Paths.get(filePathToUpdateOrCheck), newContentFromTextArea.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Konten .txt berhasil disimpan ke file: " + filePathToUpdateOrCheck);
                    contentForModelUpdate = newContentFromTextArea; // Untuk update di model
                } else if (originalFileNameLower.endsWith(".docx")) {
                    // Saat menyimpan .docx, kita membuat dokumen baru dari teks di JTextArea.
                    // Proses ini seharusnya tidak menyebabkan ZipException (yang merupakan error baca).
                    // ZipException akan terjadi saat MEMBACA file .docx yang rusak, bukan saat MENULIS yang baru seperti ini.
                    try (XWPFDocument newDoc = new XWPFDocument()) { // Membuat .docx baru di memori
                        String[] lines = newContentFromTextArea.split("\\r?\\n");
                        if (lines.length == 1 && lines[0].isEmpty() && newContentFromTextArea.isEmpty()) {
                            newDoc.createParagraph(); // Buat paragraf kosong jika tidak ada teks
                        } else {
                            for (String line : lines) {
                                XWPFParagraph paragraph = newDoc.createParagraph();
                                XWPFRun run = paragraph.createRun();
                                run.setText(line);
                            }
                        }
                        try (FileOutputStream fos = new FileOutputStream(filePathToUpdateOrCheck)) {
                            newDoc.write(fos); // Menulis ke file fisik
                            System.out.println("Konten .docx (hanya teks) berhasil disimpan ke file: " + filePathToUpdateOrCheck);
                        }
                    }
                    // Untuk model, kita bisa mengirim konten teks atau path, tergantung bagaimana model menghandle update .docx
                    // Jika model hanya menyimpan metadata dan path, maka `contentForModelUpdate` bisa null atau path.
                    // Jika model juga menyimpan cache konten teks, maka bisa diisi.
                    contentForModelUpdate = newContentFromTextArea; // Atau null, atau filePathToUpdateOrCheck
                } else {
                    // Tipe file lain yang tidak diedit kontennya, mungkin hanya nama yang diupdate.
                    // Konten untuk model bisa null karena tidak ada perubahan konten dari JTextArea.
                    System.out.println("Tipe file " + originalFileNameLower + " tidak diedit kontennya secara langsung. Hanya nama yang mungkin diupdate.");
                    contentForModelUpdate = null; 
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(view.getUpdateFrame(), "Gagal menyimpan perubahan ke file fisik: " + ex.getMessage(), "Error Penyimpanan File", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                return; // Jangan lanjutkan jika penyimpanan file fisik gagal
            }
        } else if (filePathToUpdateOrCheck != null && filePathToUpdateOrCheck.startsWith("placeholder/")) {
            System.out.println("Penyimpanan ke file fisik dilewati (placeholder). Hanya metadata yang akan diupdate.");
            // Jika konten placeholder bisa diedit di JTextArea dan ingin disimpan di model
            if (isContentActuallyEdited) contentForModelUpdate = newContentFromTextArea;
        } else if (!isContentActuallyEdited) {
            System.out.println("Konten tidak diedit (read-only view). Hanya metadata nama yang mungkin diupdate.");
            contentForModelUpdate = null; // Tidak ada perubahan konten
        }


        // Menggunakan documentModel untuk operasi update metadata dokumen di database/sistem Anda
        boolean modelUpdateSuccess = documentModel.updateDocument(
                oldName,
                newNameFromField,
                currentUserEmail, // Email pemilik/yang melakukan update
                timestamp,
                contentForModelUpdate // Bisa konten teks, path baru, atau null tergantung implementasi model
        );

        if (modelUpdateSuccess) {
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "Dokumen '" + newNameFromField + "' berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            // Update documentOrigin dengan data baru dari model, terutama jika nama berubah
            Document updatedDocFromModel = documentModel.findDocumentByNameAndOwner(newNameFromField, documentOrigin.getOwnerEmail()); // Asumsi ada metode seperti ini
            if (updatedDocFromModel == null) { // Fallback jika nama berubah dan owner tidak diketahui langsung
                updatedDocFromModel = documentModel.getActiveDocuments().get(newNameFromField); // Ini mungkin tidak ideal jika ada nama duplikat
            }

            if (updatedDocFromModel != null) {
                this.documentOrigin = updatedDocFromModel;
                view.getUpdateFrame().setTitle("Edit Dokumen: " + this.documentOrigin.getName());
                // Jika path berubah karena nama file berubah, filePathToUpdateOrCheck juga perlu diupdate
                // Namun, ini lebih baik ditangani oleh DocumentModel atau dengan mengambil ulang objek Document.
            } else {
                System.err.println("Gagal mengambil info dokumen terbaru setelah update dari DocumentModel. Mungkin nama berubah.");
                // Jika nama berubah, documentOrigin dengan nama lama tidak valid lagi.
                // Idealnya, model mengembalikan objek Document yang sudah diupdate.
                // Untuk sementara, kita set documentOrigin dengan nama baru dan path yang sama (jika tidak ada perubahan path dari model)
                this.documentOrigin.setName(newNameFromField); 
                // this.documentOrigin.setFilePath(newPathIfChanged); // Jika path juga berubah
                this.documentOrigin.setLastModified(timestamp);
                view.getUpdateFrame().setTitle("Edit Dokumen: " + this.documentOrigin.getName());
            }
        } else {
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "Gagal mengupdate metadata dokumen. Nama baru mungkin sudah ada atau terjadi kesalahan lain.", "Error Update Metadata", JOptionPane.ERROR_MESSAGE);
            // Kembalikan nama file di field ke nama lama jika update gagal tapi file fisik mungkin sudah berubah
            view.getFileNameField().setText(oldName);
        }
    }

    private void handleDownloadDocument() {
        if (documentOrigin == null || documentOrigin.getFilePath() == null ||
            documentOrigin.getFilePath().isEmpty() ||
            documentOrigin.getFilePath().startsWith("placeholder/")) {
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "Path file tidak valid atau placeholder. Download tidak tersedia.", "Error Download", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File sourceFile = new File(documentOrigin.getFilePath());
        if (!sourceFile.exists() || !sourceFile.isFile()){
            JOptionPane.showMessageDialog(view.getUpdateFrame(), "File sumber tidak ditemukan untuk di-download.", "Error Download", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Dokumen Asli Sebagai");
        fileChooser.setSelectedFile(new File(documentOrigin.getName())); // Nama file saran

        int userSelection = fileChooser.showSaveDialog(view.getUpdateFrame());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSaveTo = fileChooser.getSelectedFile();
            // Pastikan ekstensi sama dengan file asli jika tidak diubah pengguna
            String originalExtension = "";
            int i = documentOrigin.getName().lastIndexOf('.');
            if (i > 0) {
                originalExtension = documentOrigin.getName().substring(i);
            }
            if (!fileToSaveTo.getName().toLowerCase().endsWith(originalExtension.toLowerCase()) && !originalExtension.isEmpty()) {
                fileToSaveTo = new File(fileToSaveTo.getParentFile(), fileToSaveTo.getName() + originalExtension);
            }


            try {
                Files.copy(sourceFile.toPath(), fileToSaveTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(view.getUpdateFrame(), "Dokumen '" + documentOrigin.getName() + "' berhasil di-download ke:\n" + fileToSaveTo.getAbsolutePath(), "Download Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(view.getUpdateFrame(), "Gagal men-download file: " + ex.getMessage(), "Error Download", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception exGenerale) {
                JOptionPane.showMessageDialog(view.getUpdateFrame(), "Terjadi kesalahan umum saat download: " + exGenerale.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                exGenerale.printStackTrace();
            }
        }
    }
    
    // Placeholder, implementasi nyata bergantung pada bagaimana Anda mengelola instance controller
    private Object getParentDocumentControllerInstance() {
        // Ini perlu cara untuk mendapatkan instance DocumentController yang aktif
        // Misalnya, jika DocumentController membuat UpdateDocumentController,
        // ia bisa meneruskan dirinya (this) dan disimpan di field di UpdateDocumentController.
        // Atau menggunakan mekanisme lookup/dependency injection jika ada.
        if (this.parentDocumentView != null && this.parentDocumentView.getController() instanceof DocumentController) {
             return this.parentDocumentView.getController();
        }
        // Logika fallback atau default jika tidak ditemukan
        return null;
    }
}