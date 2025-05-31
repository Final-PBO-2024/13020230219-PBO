package Model;

import Database.DatabaseConnection; // Pastikan ini adalah path yang benar
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class ActivityLogModel extends DatabaseConnection {

    // Enum untuk jenis aksi agar konsisten
    public enum ActionType {
        // Aksi Dokumen Utama
        ADD_DOCUMENT,
        VIEW_DOCUMENT_CONTENT, // Ketika konten dokumen (misal .txt atau .docx) dimuat untuk dilihat/diedit
        UPDATE_DOCUMENT_NAME,
        UPDATE_DOCUMENT_CONTENT,
        DELETE_DOCUMENT_SOFT, // Pindah ke Recycle Bin
        DELETE_DOCUMENT_PERMANENT,
        RESTORE_DOCUMENT,
        DOWNLOAD_DOCUMENT,

        // Aksi Berbagi & Akses
        SHARE_DOCUMENT,         // Memberi akses
        REVOKE_ACCESS_DOCUMENT, // Mencabut akses

        // Aksi Profil Pengguna (Contoh)
        USER_LOGIN,
        USER_LOGOUT,
        USER_REGISTER,
        UPDATE_USER_PROFILE,

        // Lain-lain
        UNDEFINED_ACTION
    }

    public ActivityLogModel() {
        // Konstruktor
    }

    /**
     * Mencatat aktivitas ke database.
     * @param userEmail Email pengguna yang melakukan aksi.
     * @param actionType Jenis aksi dari enum ActionType.
     * @param documentId ID dokumen yang terkait (bisa null).
     * @param targetDocumentName Nama dokumen atau target lain yang relevan (bisa null).
     * @param details Detail tambahan tentang aktivitas (bisa null).
     * @return true jika berhasil mencatat, false jika gagal.
     */
    public boolean logActivity(String userEmail, ActionType actionType, Integer documentId, String targetDocumentName, String details) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            System.err.println("ActivityLogModel: userEmail tidak boleh kosong untuk mencatat aktivitas.");
            return false;
        }
        if (actionType == null) {
            actionType = ActionType.UNDEFINED_ACTION; // Default jika null
        }

        String sql = "INSERT INTO activity_logs (user_email, action_type, document_id, target_document_name, activity_timestamp, details) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userEmail);
            pstmt.setString(2, actionType.name()); // Simpan nama enum sebagai string

            if (documentId != null) {
                pstmt.setInt(3, documentId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            if (targetDocumentName != null && targetDocumentName.length() > 255) { // Antisipasi panjang field
                targetDocumentName = targetDocumentName.substring(0, 252) + "...";
            }
            pstmt.setString(4, targetDocumentName);

            pstmt.setTimestamp(5, new Timestamp(new Date().getTime())); // Timestamp saat ini

            if (details != null && details.length() > 65535) { // Antisipasi panjang field TEXT
                details = details.substring(0, 65532) + "...";
            }
            pstmt.setString(6, details);
            
            pstmt.executeUpdate();
            // System.out.println("ACTIVITY LOGGED: User='" + userEmail + "', Action='" + actionType.name() + 
            //                    (targetDocumentName != null ? "', TargetDocName='" + targetDocumentName : "") +
            //                    (documentId != null ? "', TargetDocID='" + documentId : "") +
            //                    (details != null ? "', Details='" + details + "'" : "'"));
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal mencatat aktivitas ke DB: User='" + userEmail + "', Action='" + actionType.name() + "'. Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Overload method jika hanya nama dokumen yang tersedia atau relevan
    public boolean logActivity(String userEmail, ActionType actionType, String targetDocumentName, String details) {
        return logActivity(userEmail, actionType, null, targetDocumentName, details);
    }

    // Overload method jika hanya ID dokumen yang tersedia atau relevan
    public boolean logActivity(String userEmail, ActionType actionType, Integer documentId, String details) {
        return logActivity(userEmail, actionType, documentId, null, details);
    }
     // Overload method jika hanya email dan tipe aksi (misal login/logout)
    public boolean logActivity(String userEmail, ActionType actionType, String details) {
        return logActivity(userEmail, actionType, null, null, details);
    }
}