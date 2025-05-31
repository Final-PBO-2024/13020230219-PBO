// File: Model/DocumentModel.java
package Model;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import Database.DatabaseConnection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
// import Model.ActivityLogModel; // Tidak perlu jika di package yang sama

public class DocumentModel extends DatabaseConnection{
    private ActivityLogModel activityLogModel; // LOGGING: Tambahkan field

    public DocumentModel() {
        this.activityLogModel = new ActivityLogModel(); // LOGGING: Instansiasi
    }

    // ... (getUserIdByEmailFromDb, getEmailByUserIdFromDb, getDocumentIdByNameAndOwnerFromDb, getDocumentDetailsByNameAndOwner, findDocumentByNameAndOwner tetap sama seperti di respons perbaikan sebelumnya) ...
    private int getUserIdByEmailFromDb(String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1; 
    }

    private String getEmailByUserIdFromDb(Connection conn, int userId) throws SQLException {
        String sql = "SELECT email FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        }
        return null;
    }
    
    private int getDocumentIdByNameAndOwnerFromDb(String docName, int ownerUserId) throws SQLException {
        String sql = "SELECT document_id FROM documents WHERE document_name = ? AND owner_user_id = ? AND is_deleted = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, docName);
            pstmt.setInt(2, ownerUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("document_id");
            }
        }
        return -1;
    }
    
    private Document getDocumentDetailsByNameAndOwner(Connection conn, String docName, int ownerUserId, boolean includeDeleted) throws SQLException {
        String sql = "SELECT d.document_id, d.document_name, u.email as owner_email, " +
                     (includeDeleted ? "d.deleted_timestamp" : "d.timestamp_modified") + " as relevant_timestamp, " +
                     "d.file_path, d.file_type, d.file_size, d.is_deleted " +
                     "FROM documents d JOIN users u ON d.owner_user_id = u.user_id " +
                     "WHERE d.document_name = ? AND d.owner_user_id = ?";
        if (!includeDeleted) {
            sql += " AND d.is_deleted = FALSE";
        } else {
            sql += " AND d.is_deleted = TRUE"; // Pastikan hanya yang dihapus jika includeDeleted true
        }


        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, docName);
            pstmt.setInt(2, ownerUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Document(
                    rs.getInt("document_id"),
                    rs.getString("document_name"),
                    rs.getString("owner_email"),
                    rs.getString("relevant_timestamp"),
                    rs.getString("file_path"),
                    rs.getString("file_type"),
                    rs.getInt("file_size"),    
                    rs.getBoolean("is_deleted")
                );
            }
        }
        return null;
    }
    
    public Document findDocumentByNameAndOwner(String docName, String ownerEmail) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int ownerUserId = getUserIdByEmailFromDb(ownerEmail);
            if (ownerUserId == -1) {
                System.err.println("DocumentModel.findDocumentByNameAndOwner: Owner email '" + ownerEmail + "' tidak ditemukan.");
                return null;
            }
            Document doc = getDocumentDetailsByNameAndOwner(conn, docName, ownerUserId, false); 
            if (doc != null) {
                loadAccessListForDocument(conn, doc);
            }
            return doc;
        } catch (SQLException e) {
            System.err.println("Error SQL di findDocumentByNameAndOwner (DocumentModel): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public boolean addDocumentToDb(String name, int ownerUserId, String timestamp, String filePath, String fileType, int fileSize) {
        // ... (logika cek duplikasi) ...
        String checkSql = "SELECT document_id FROM documents WHERE document_name = ? AND owner_user_id = ? AND is_deleted = FALSE";
        try (Connection connCheck = DatabaseConnection.getConnection(); // Gunakan koneksi terpisah untuk cek
             PreparedStatement pstmtCheck = connCheck.prepareStatement(checkSql)) {
            pstmtCheck.setString(1, name);
            pstmtCheck.setInt(2, ownerUserId);
            ResultSet rsCheck = pstmtCheck.executeQuery();
            if (rsCheck.next()) {
                System.err.println("DocumentModel: Gagal menambahkan dokumen. Nama dokumen '" + name + "' sudah ada untuk pengguna ini.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("DocumentModel: Error saat cek duplikasi dokumen: " + e.getMessage());
            e.printStackTrace(); 
            return false;
        }


        String sql = "INSERT INTO documents (owner_user_id, document_name, timestamp_created, timestamp_modified, file_path, file_type, file_size, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, FALSE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ownerUserId);
            pstmt.setString(2, name);
            pstmt.setString(3, timestamp); 
            pstmt.setString(4, timestamp); 
            pstmt.setString(5, filePath); 
            pstmt.setString(6, fileType);
            pstmt.setInt(7, fileSize);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                int documentId = -1;
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        documentId = generatedKeys.getInt(1);
                        grantAccessInDb(documentId, ownerUserId, "owner", timestamp);
                    }
                }
                // --- Pencatatan Aktivitas INPUT FILE ---
                String ownerEmail = getEmailByUserIdFromDb(conn, ownerUserId); 
                if (ownerEmail != null && documentId != -1) {
                    String details = String.format("Path: %s, Tipe: %s, Size: %d bytes", filePath, fileType, fileSize);
                    activityLogModel.logActivity(ownerEmail, ActivityLogModel.ActionType.ADD_DOCUMENT, documentId, name, details);
                }
                // --- Akhir Pencatatan ---
                return true;
            }
        } catch (SQLException e) {
            System.err.println("DocumentModel: Error saat menambahkan dokumen (Path) ke DB: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean grantAccessInDb(int documentId, int userIdSharedWith, String accessType, String timestamp) {
        // ... (isi metode grantAccessInDb tetap sama seperti di respons perbaikan sebelumnya) ...
        String checkSql = "SELECT access_id FROM document_access WHERE document_id = ? AND user_id_shared_with = ? AND access_type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtCheck = conn.prepareStatement(checkSql)) {
            pstmtCheck.setInt(1, documentId);
            pstmtCheck.setInt(2, userIdSharedWith);
            pstmtCheck.setString(3, accessType);
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next()) {
                System.out.println("DocumentModel: Pengguna ID " + userIdSharedWith + " sudah memiliki akses '" + accessType + "' untuk dokumen ID " + documentId);
                return true; // Akses sudah ada
            }
        } catch (SQLException e) {
            System.err.println("DocumentModel: Error saat cek duplikasi akses: " + e.getMessage());
        }

        String sql = "INSERT INTO document_access (document_id, user_id_shared_with, access_type, timestamp_granted) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, documentId);
            pstmt.setInt(2, userIdSharedWith);
            pstmt.setString(3, accessType);
            pstmt.setString(4, timestamp);
            pstmt.executeUpdate();
            System.out.println("DocumentModel: Akses '" + accessType + "' untuk dokumen ID " + documentId + " diberikan ke pengguna ID " + userIdSharedWith);
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { 
                System.err.println("DocumentModel: Gagal memberi akses - kemungkinan entri duplikat atau pelanggaran constraint. " + e.getMessage());
            } else {
                System.err.println("DocumentModel: Error saat memberi akses di DB: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean grantAccess(String documentName, String ownerEmail, String emailToGrant, String accessType) {
        try {
            int ownerUserId = getUserIdByEmailFromDb(ownerEmail);
            if (ownerUserId == -1) { /* ... */ return false; }
            int docId = getDocumentIdByNameAndOwnerFromDb(documentName, ownerUserId);
            if (docId == -1) { /* ... */ return false; }
            int userIdToGrant = getUserIdByEmailFromDb(emailToGrant);
            if (userIdToGrant == -1) { /* ... */ return false; }
            if (ownerUserId == userIdToGrant && !accessType.equalsIgnoreCase("owner")) { /* ... */ return false; }
            
            String timestampLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            boolean success = grantAccessInDb(docId, userIdToGrant, accessType, timestampLog);
            if (success) {
                // --- Pencatatan Aktivitas SHARE DOCUMENT ---
                String details = String.format("Memberikan akses '%s' kepada '%s'", accessType, emailToGrant);
                activityLogModel.logActivity(ownerEmail, ActivityLogModel.ActionType.SHARE_DOCUMENT, docId, documentName, details);
                // --- Akhir Pencatatan ---
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error SQL di grantAccess (DocumentModel): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ... (getActiveDocuments, loadAccessListForDocument tetap sama seperti di respons perbaikan sebelumnya) ...
    public Map<String, Document> getActiveDocuments() {
        Map<String, Document> activeDocs = new HashMap<>();
        String sql = "SELECT d.document_id, d.document_name, u.email as owner_email, d.timestamp_modified, " +
                     "d.file_path, d.file_type, d.file_size, d.is_deleted " +
                     "FROM documents d JOIN users u ON d.owner_user_id = u.user_id WHERE d.is_deleted = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Document doc = new Document(
                    rs.getInt("document_id"),
                    rs.getString("document_name"),
                    rs.getString("owner_email"),
                    rs.getString("timestamp_modified"),
                    rs.getString("file_path"),
                    rs.getString("file_type"),
                    rs.getInt("file_size"),    
                    rs.getBoolean("is_deleted")
                );
                loadAccessListForDocument(conn, doc); 
                activeDocs.put(doc.getName(), doc); // Pertimbangkan kunci unik jika nama tidak unik global
            }
        } catch (SQLException e) {
            System.err.println("Error saat getActiveDocuments (DocumentModel): " + e.getMessage());
            e.printStackTrace();
        }
        return activeDocs;
    }
    
    private void loadAccessListForDocument(Connection conn, Document document) throws SQLException {
        if (conn == null || conn.isClosed()) {
            System.err.println("DocumentModel.loadAccessListForDocument: Koneksi database null atau tertutup.");
            // Coba dapatkan koneksi baru jika null. Ini bisa berisiko jika getConnection() juga gagal.
            try (Connection newConn = DatabaseConnection.getConnection()) {
                 if (newConn == null || newConn.isClosed()) {
                     throw new SQLException("Tidak dapat membuat koneksi baru untuk loadAccessListForDocument.");
                 }
                 // Logika ini bisa dipertimbangkan ulang, mungkin lebih baik melempar error jika conn awal tidak valid
                 // atau memastikan conn selalu valid saat memanggil method ini.
                 // Untuk saat ini, kita asumsikan conn yang dilewatkan adalah yang utama.
            } catch (SQLException se) {
                 throw new SQLException("Gagal mendapatkan koneksi baru di loadAccessListForDocument: " + se.getMessage());
            }
        }

        String sql = "SELECT u.email as shared_with_email, da.access_type, da.timestamp_granted " +
                     "FROM document_access da JOIN users u ON da.user_id_shared_with = u.user_id " +
                     "WHERE da.document_id = ?";
        Map<String, Document.AccessDetail> accessDetails = new HashMap<>();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, document.getDocumentId());
            ResultSet rsAccess = pstmt.executeQuery(); 
            while(rsAccess.next()){ 
                String emailSharedWith = rsAccess.getString("shared_with_email");
                if(emailSharedWith != null){
                    accessDetails.put(emailSharedWith, new Document.AccessDetail(rsAccess.getString("access_type"), rsAccess.getString("timestamp_granted")));
                }
            }
        }
        document.setAccessListWithTimestamp(accessDetails);
    }


    public boolean removeDocument(String documentName, String ownerEmail) { // Soft Delete
        try {
            int ownerUserId = getUserIdByEmailFromDb(ownerEmail);
            if (ownerUserId == -1) { /* ... */ return false; }
            int docId = getDocumentIdByNameAndOwnerFromDb(documentName, ownerUserId);
            if (docId == -1) { /* ... */ return false; }

            String sql = "UPDATE documents SET is_deleted = TRUE, deleted_timestamp = ?, timestamp_modified = ? WHERE document_id = ? AND is_deleted = FALSE";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                pstmt.setString(1, now); 
                pstmt.setString(2, now); 
                pstmt.setInt(3, docId); 
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    // --- Pencatatan Aktivitas SOFT DELETE ---
                    activityLogModel.logActivity(ownerEmail, ActivityLogModel.ActionType.DELETE_DOCUMENT_SOFT, docId, documentName, "Dokumen dipindahkan ke Recycle Bin.");
                    // --- Akhir Pencatatan ---
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat removeDocument (DocumentModel): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateDocument(String oldName, String newName, String ownerEmail, String timestamp, String newFilePathOrContentOrNull) {
        int docId = -1; // Inisialisasi docId
        String currentFilePath = null;
        String currentFileType = null;
        int currentFileSize = 0;
        ActivityLogModel.ActionType actionTypeForLog = ActivityLogModel.ActionType.UPDATE_DOCUMENT_METADATA; // Default
        String logDetails = "";

        try (Connection conn = DatabaseConnection.getConnection()){ // Gunakan satu koneksi untuk operasi ini
            int ownerUserId = getUserIdByEmailFromDb(ownerEmail);
            if (ownerUserId == -1) { /* ... */ return false; }
            
            // Dapatkan detail dokumen lama
            Document oldDoc = getDocumentDetailsByNameAndOwner(conn, oldName, ownerUserId, false);
            if (oldDoc == null) {
                System.err.println("Dokumen lama '" + oldName + "' tidak ditemukan untuk pengguna " + ownerEmail);
                return false;
            }
            docId = oldDoc.getDocumentId();
            currentFilePath = oldDoc.getFilePath();
            currentFileType = oldDoc.getFileType();
            currentFileSize = oldDoc.getFileSize();

            // Cek duplikasi nama baru
            if (!oldName.equals(newName)) {
                int existingNewNameDocId = getDocumentIdByNameAndOwnerFromDb(newName, ownerUserId); // Ini pakai koneksi sendiri
                if (existingNewNameDocId != -1 && existingNewNameDocId != docId) {
                    System.err.println("Nama dokumen baru '" + newName + "' sudah digunakan.");
                    return false;
                }
                logDetails += String.format("Nama diubah dari '%s' ke '%s'. ", oldName, newName);
            }
            
            String finalFilePathToSaveInDb = currentFilePath;
            String finalFileTypeToSaveInDb = currentFileType;
            int finalFileSizeToSaveInDb = currentFileSize;
            boolean contentPhysicallyChanged = false;

            // Logika untuk menangani perubahan file path atau konten
            if (newFilePathOrContentOrNull != null) {
                // Asumsi jika newFilePathOrContentOrNull adalah path (misal dari upload revisi oleh Controller)
                File newFileCandidate = new File(newFilePathOrContentOrNull);
                if (newFileCandidate.exists() && newFileCandidate.isFile() && !newFilePathOrContentOrNull.equals(currentFilePath)) {
                    finalFilePathToSaveInDb = newFilePathOrContentOrNull.replace("\\", "/");
                    try {
                        finalFileTypeToSaveInDb = Files.probeContentType(newFileCandidate.toPath());
                        if (finalFileTypeToSaveInDb == null) finalFileTypeToSaveInDb = new Document("","","","").getFileType(); // default dari Document
                    } catch (IOException e) { finalFileTypeToSaveInDb = new Document("","","","").getFileType(); }
                    finalFileSizeToSaveInDb = (int) newFileCandidate.length();
                    logDetails += String.format("File diganti/diperbarui ke path '%s'. ", finalFilePathToSaveInDb);
                    contentPhysicallyChanged = true;
                } 
                // Jika newFilePathOrContentOrNull adalah konten teks BARU (misal untuk .txt atau teks dari .docx yang sudah diekstrak controller)
                // dan path fisik TIDAK berubah (controller menulis ke path yang sama, yaitu currentFilePath)
                else if (currentFilePath != null && !currentFilePath.startsWith("placeholder/")) {
                     if (oldName.toLowerCase().endsWith(".txt") || newName.toLowerCase().endsWith(".txt")) {
                        try {
                            Files.write(Paths.get(currentFilePath), newFilePathOrContentOrNull.getBytes(StandardCharsets.UTF_8));
                            finalFileSizeToSaveInDb = newFilePathOrContentOrNull.getBytes(StandardCharsets.UTF_8).length;
                            logDetails += "Konten file .txt diubah. ";
                            contentPhysicallyChanged = true;
                        } catch (IOException e) { /* Gagal tulis, jangan update DB? */ }
                    } else if (oldName.toLowerCase().endsWith(".docx") || newName.toLowerCase().endsWith(".docx")) {
                        // Jika controller sudah menulis ulang file .docx di currentFilePath dengan konten baru
                        File updatedDocxFile = new File(currentFilePath);
                        if(updatedDocxFile.exists()) {
                            finalFileSizeToSaveInDb = (int) updatedDocxFile.length();
                            logDetails += "Konten file .docx kemungkinan diubah. "; // Controller yang memastikan ini
                            contentPhysicallyChanged = true;
                        }
                    }
                }
            }
            
            // Logika rename file fisik jika nama dokumen di DB berubah
            if (!oldName.equals(newName) && currentFilePath != null && !currentFilePath.startsWith("placeholder/") && finalFilePathToSaveInDb.equals(currentFilePath)) {
                File oldPhysicalFile = new File(currentFilePath);
                String parentDir = oldPhysicalFile.getParent();
                if (parentDir != null) {
                    File newPhysicalFile = new File(parentDir, newName);
                    if (oldPhysicalFile.exists() && oldPhysicalFile.renameTo(newPhysicalFile)) {
                        finalFilePathToSaveInDb = newPhysicalFile.getAbsolutePath().replace("\\", "/");
                        logDetails += String.format("Nama file fisik diubah ke '%s'. ", newName);
                    }
                }
            }

            // Tentukan ActionType untuk log
            if (contentPhysicallyChanged && !oldName.equals(newName)) {
                actionTypeForLog = ActivityLogModel.ActionType.UPDATE_DOCUMENT_FULL;
            } else if (contentPhysicallyChanged) {
                actionTypeForLog = ActivityLogModel.ActionType.UPDATE_DOCUMENT_CONTENT;
            } else if (!oldName.equals(newName)) {
                actionTypeForLog = ActivityLogModel.ActionType.UPDATE_DOCUMENT_METADATA;
            } else { // Jika tidak ada perubahan nama atau konten yang terdeteksi, mungkin hanya timestamp
                if (logDetails.isEmpty()) logDetails = "Timestamp dokumen diupdate (tidak ada perubahan data lain terdeteksi).";
                 // actionTypeForLog tetap UPDATE_DOCUMENT_METADATA
            }


            StringBuilder sqlBuilder = new StringBuilder("UPDATE documents SET document_name = ?, timestamp_modified = ?, file_path = ?, file_type = ?, file_size = ? WHERE document_id = ?");
            try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
                pstmt.setString(1, newName);
                pstmt.setString(2, timestamp);
                pstmt.setString(3, finalFilePathToSaveInDb);
                pstmt.setString(4, finalFileTypeToSaveInDb);
                pstmt.setInt(5, finalFileSizeToSaveInDb);
                pstmt.setInt(6, docId);
                
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    activityLogModel.logActivity(ownerEmail, actionTypeForLog, docId, newName, logDetails.trim());
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat updateDocument (DocumentModel): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean restoreDocument(String docName, String ownerEmail) {
        try (Connection conn = DatabaseConnection.getConnection()) { // Gunakan satu koneksi
            int ownerUserId = getUserIdByEmailFromDb(ownerEmail);
            if (ownerUserId == -1) { /* ... */ return false; }

            Document docToRestore = getDocumentDetailsByNameAndOwner(conn, docName, ownerUserId, true); // Cari yang sudah dihapus
            if (docToRestore == null) {
                System.err.println("Dokumen '" + docName + "' milik '" + ownerEmail + "' tidak ditemukan di recycle bin.");
                return false;
            }
            int docId = docToRestore.getDocumentId();
            
            int activeDocWithSameNameId = getDocumentIdByNameAndOwnerFromDb(docName, ownerUserId); // Cek yang aktif
            if (activeDocWithSameNameId != -1) {
                 System.err.println("Dokumen aktif dengan nama '" + docName + "' sudah ada untuk pengguna '" + ownerEmail + "'.");
                 return false; 
            }

            String sqlRestore = "UPDATE documents SET is_deleted = FALSE, deleted_timestamp = NULL, timestamp_modified = ? WHERE document_id = ?";
            try (PreparedStatement pstmtRestore = conn.prepareStatement(sqlRestore)) {
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                pstmtRestore.setString(1, now);
                pstmtRestore.setInt(2, docId);
                int affectedRows = pstmtRestore.executeUpdate();
                if (affectedRows > 0) {
                    activityLogModel.logActivity(ownerEmail, ActivityLogModel.ActionType.RESTORE_DOCUMENT, docId, docName, "Dokumen direstore dari Recycle Bin.");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat restoreDocument (DocumentModel): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean permanentlyDeleteDocument(String docName, String ownerEmail) {
        Connection conn = null;
        int docIdForLog = -1;
        String filePathForLog = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            int ownerUserId = getUserIdByEmailFromDb(ownerEmail); 
            if (ownerUserId == -1) { /* ... */ conn.rollback(); return false; }

            Document docToDelete = getDocumentDetailsByNameAndOwner(conn, docName, ownerUserId, true); // Cari yang sudah dihapus
            if (docToDelete == null) {
                System.err.println("Dokumen '" + docName + "' milik '" + ownerEmail + "' tidak ditemukan di recycle bin.");
                conn.rollback(); return false;
            }
            docIdForLog = docToDelete.getDocumentId();
            filePathForLog = docToDelete.getFilePath();

            String sqlDeleteAccess = "DELETE FROM document_access WHERE document_id = ?";
            try (PreparedStatement pstmtDeleteAccess = conn.prepareStatement(sqlDeleteAccess)) {
                pstmtDeleteAccess.setInt(1, docIdForLog);
                pstmtDeleteAccess.executeUpdate(); 
            }

            String sqlDeleteDoc = "DELETE FROM documents WHERE document_id = ?";
            try (PreparedStatement pstmtDeleteDoc = conn.prepareStatement(sqlDeleteDoc)) {
                pstmtDeleteDoc.setInt(1, docIdForLog);
                int affectedRows = pstmtDeleteDoc.executeUpdate();
                if (affectedRows == 0) { /* ... */ conn.rollback(); return false; }
            }

            if (filePathForLog != null && !filePathForLog.trim().isEmpty() && !filePathForLog.startsWith("placeholder/")) {
                try {
                    Files.deleteIfExists(Paths.get(filePathForLog));
                } catch (IOException e) { /* ... */ conn.rollback(); return false; }
            }

            conn.commit(); 
            activityLogModel.logActivity(ownerEmail, ActivityLogModel.ActionType.DELETE_DOCUMENT_PERMANENT, docIdForLog, docName, "File fisik (jika ada): " + (filePathForLog != null ? filePathForLog : "N/A"));
            return true;

        } catch (SQLException e) {
            // ... (rollback, error handling) ...
            System.err.println("Error SQL saat permanentlyDeleteDocument (DocumentModel): " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException exRollback) { exRollback.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException exClose) { exClose.printStackTrace(); }
            }
        }
        return false;
    }

    // ... (getSharesByDocument, getDocumentsOwnedBy, getDocumentsSharedWithUser, getAllDocumentsIncludingRecycled tetap sama seperti di respons perbaikan sebelumnya) ...
    // Pastikan metode-metode ini menggunakan try-with-resources untuk koneksi jika mereka membuatnya sendiri,
    // atau menerima koneksi sebagai parameter jika dipanggil dalam konteks transaksi yang lebih besar.
    // Contoh:
    public Map<String, Document.AccessDetail> getSharesByDocument(String ownerEmail, String documentName) {
        try (Connection conn = DatabaseConnection.getConnection()){
            int ownerId = getUserIdByEmailFromDb(ownerEmail); 
            if(ownerId == -1) return new HashMap<>();
            
            int docId = -1; // Dapatkan docId menggunakan koneksi yang sama
            String sqlGetDocId = "SELECT document_id FROM documents WHERE document_name = ? AND owner_user_id = ? AND is_deleted = FALSE";
            try(PreparedStatement pstmtGetId = conn.prepareStatement(sqlGetDocId)){
                pstmtGetId.setString(1, documentName);
                pstmtGetId.setInt(2, ownerId);
                ResultSet rsId = pstmtGetId.executeQuery();
                if(rsId.next()){
                    docId = rsId.getInt("document_id");
                } else {
                    return new HashMap<>(); // Dokumen tidak ditemukan
                }
            }
            
            if(docId == -1) return new HashMap<>();

            Document tempDoc = new Document(documentName,ownerEmail,"",""); 
            tempDoc.setDocumentId(docId);
            loadAccessListForDocument(conn, tempDoc); // Gunakan koneksi yang sudah ada
            
            return tempDoc.getAccessListWithTimestamp().entrySet().stream()
                .filter(entry -> !entry.getKey().equals(ownerEmail)) 
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public Map<String, Document> getDocumentsOwnedBy(String ownerEmail) {
        Map<String, Document> ownedDocs = new HashMap<>();
        String sql = "SELECT d.document_id, d.document_name, u.email as owner_email, d.timestamp_modified, " +
                     "d.file_path, d.file_type, d.file_size, d.is_deleted " +
                     "FROM documents d JOIN users u ON d.owner_user_id = u.user_id " +
                     "WHERE u.email = ? AND d.is_deleted = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ownerEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                 Document doc = new Document(
                    rs.getInt("document_id"),
                    rs.getString("document_name"),
                    rs.getString("owner_email"),
                    rs.getString("timestamp_modified"),
                    rs.getString("file_path"),
                    rs.getString("file_type"),
                    rs.getInt("file_size"),
                    rs.getBoolean("is_deleted")
                );
                loadAccessListForDocument(conn, doc);
                ownedDocs.put(doc.getName(), doc); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ownedDocs;
    }

    public Map<String, Document> getDocumentsSharedWithUser(String targetUserEmail) {
        Map<String, Document> sharedDocs = new HashMap<>();
        int targetUserId;
        try {
            targetUserId = getUserIdByEmailFromDb(targetUserEmail);
            if (targetUserId == -1) return sharedDocs; 
        } catch (SQLException e) {
            e.printStackTrace(); return sharedDocs;
        }

        String sql = "SELECT d.document_id, d.document_name, owner_u.email as owner_email, d.timestamp_modified, d.file_path, d.file_type, d.file_size, d.is_deleted " +
                     "FROM documents d " +
                     "JOIN document_access da ON d.document_id = da.document_id " +
                     "JOIN users owner_u ON d.owner_user_id = owner_u.user_id " +
                     "WHERE da.user_id_shared_with = ? AND d.is_deleted = FALSE AND d.owner_user_id != ?"; 
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, targetUserId);
            pstmt.setInt(2, targetUserId); 
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Document doc = new Document(
                    rs.getInt("document_id"),
                    rs.getString("document_name"),
                    rs.getString("owner_email"),
                    rs.getString("timestamp_modified"),
                    rs.getString("file_path"),
                    rs.getString("file_type"),
                    rs.getInt("file_size"),
                    rs.getBoolean("is_deleted")
                );
                loadAccessListForDocument(conn, doc); 
                sharedDocs.put(doc.getName(), doc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sharedDocs;
    }

    public Map<String, Document> getRecycledDocuments() { 
        Map<String, Document> recycledDocs = new HashMap<>();
        String sql = "SELECT d.document_id, d.document_name, u.email as owner_email, d.deleted_timestamp, " +
                     "d.file_path, d.file_type, d.file_size, d.is_deleted " +
                     "FROM documents d JOIN users u ON d.owner_user_id = u.user_id WHERE d.is_deleted = TRUE";
        try (Connection conn = DatabaseConnection.getConnection(); // Tambahkan try-with-resources untuk koneksi
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                 Document doc = new Document(
                    rs.getInt("document_id"),
                    rs.getString("document_name"),
                    rs.getString("owner_email"),
                    rs.getString("deleted_timestamp"), 
                    rs.getString("file_path"),
                    rs.getString("file_type"),
                    rs.getInt("file_size"),    
                    rs.getBoolean("is_deleted")
                );
                // Mungkin tidak perlu load access list untuk recycle bin, tergantung kebutuhan
                recycledDocs.put(doc.getDocumentId() + "::" + doc.getName(), doc); // Kunci lebih unik
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recycledDocs;
    }


    public Map<String, Document> getAllDocumentsIncludingRecycled() {
        Map<String, Document> allDocs = new HashMap<>();
         String sql = "SELECT d.document_id, d.document_name, u.email as owner_email, d.timestamp_modified, d.deleted_timestamp, " +
                     "d.file_path, d.file_type, d.file_size, d.is_deleted " +
                     "FROM documents d JOIN users u ON d.owner_user_id = u.user_id";
        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Document doc = new Document(
                    rs.getInt("document_id"),
                    rs.getString("document_name"),
                    rs.getString("owner_email"),
                    rs.getBoolean("is_deleted") ? rs.getString("deleted_timestamp") : rs.getString("timestamp_modified"),
                    rs.getString("file_path"),
                    rs.getString("file_type"),
                    rs.getInt("file_size"), 
                    rs.getBoolean("is_deleted")
                );
                loadAccessListForDocument(conn, doc);
                allDocs.put(doc.getDocumentId() + "::" + doc.getName(), doc); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allDocs;
    }
}