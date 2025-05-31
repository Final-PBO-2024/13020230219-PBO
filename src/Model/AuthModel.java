package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Database.DatabaseConnection;
// import Model.ActivityLogModel; // Tidak perlu jika di package yang sama

public class AuthModel extends DatabaseConnection{
    private ActivityLogModel activityLogModel; // LOGGING: Tambahkan field

    public static class User {
        // ... (isi kelas User tetap sama) ...
        private int userId;
        private String username;
        private String email;
        private String passwordHash;
        private String profilePicturePath;

        public User(String username, String email, String passwordHash) {
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
            this.profilePicturePath = null;
        }

        public User(int userId, String username, String email, String passwordHash, String profilePicturePath) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
            this.profilePicturePath = profilePicturePath;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPasswordHash() { return passwordHash; }
        public String getProfilePicturePath() { return profilePicturePath; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setUsername(String username) { this.username = username; }
        public void setEmail(String email) { this.email = email; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public void setProfilePicturePath(String profilePicturePath) {
            this.profilePicturePath = profilePicturePath;
        }
    }

    public AuthModel() {
        System.out.println("AuthModel: Inisialisasi selesai.");
        this.activityLogModel = new ActivityLogModel(); // LOGGING: Instansiasi
    }
       
    public User getUserByEmailFromDb(String email) {
        // ... (isi metode tetap sama) ...
        String sql = "SELECT user_id, username, email, password_hash, profile_picture_path FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("profile_picture_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("AuthModel.getUserByEmailFromDb Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean authenticate(String email, String passwordInput) {
        String sql = "SELECT password_hash FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                // TODO: Implementasikan perbandingan hash password yang aman (misalnya menggunakan BCrypt)
                boolean isAuthenticated = storedHash.equals(passwordInput); 
                if (isAuthenticated) {
                    // LOGGING: User Login
                    activityLogModel.logActivity(email, ActivityLogModel.ActionType.USER_LOGIN, "Login berhasil.");
                } else {
                    // LOGGING: User Login Gagal (opsional, bisa menghasilkan banyak log)
                    // activityLogModel.logActivity(email, ActivityLogModel.ActionType.USER_LOGIN, "Percobaan login gagal (password salah).");
                }
                return isAuthenticated;
            } else {
                // LOGGING: User Login Gagal (opsional)
                // activityLogModel.logActivity(email, ActivityLogModel.ActionType.USER_LOGIN, "Percobaan login gagal (email tidak ditemukan).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUsernameByEmail(String email) {
        // ... (isi metode tetap sama) ...
        User user = getUserByEmailFromDb(email);
        return (user != null) ? user.getUsername() : null;
    }
    
    public String getUserProfilePicturePath(String email) {
        // ... (isi metode tetap sama) ...
        User user = getUserByEmailFromDb(email);
        return (user != null) ? user.getProfilePicturePath() : null;
    }

    public boolean register(String username, String email, String password) {
        // ... (validasi input tetap sama) ...
        // ... (cek duplikasi email tetap sama) ...
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // TODO: Implementasikan hashing password yang aman (misalnya menggunakan BCrypt)
            String hashedPassword = password; 
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword); 
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // LOGGING: User Register
                activityLogModel.logActivity(email, ActivityLogModel.ActionType.USER_REGISTER, "Pengguna baru '" + username + "' berhasil terdaftar.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateUserProfile(String email, String newUsername, String newPassword, String newProfilePicturePath) {
        User user = getUserByEmailFromDb(email); 
        if (user == null) { return false; }

        // ... (logika untuk membangun SQL UPDATE tetap sama) ...
        boolean changed = false;
        StringBuilder sqlBuilder = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();
        boolean firstFieldToUpdate = true;
        String logDetails = "Memperbarui profil: ";
        boolean usernameChanged = false;
        boolean passwordChanged = false;
        boolean pictureChanged = false;


        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.trim().equals(user.getUsername())) {
            sqlBuilder.append("username = ?");
            params.add(newUsername.trim());
            firstFieldToUpdate = false;
            changed = true;
            logDetails += "Username diubah menjadi '" + newUsername.trim() + "'. ";
            usernameChanged = true;
        }
        
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!firstFieldToUpdate) sqlBuilder.append(", ");
            sqlBuilder.append("password_hash = ?");
            // TODO: Implementasikan hashing password yang aman (misalnya menggunakan BCrypt)
            params.add(newPassword); 
            firstFieldToUpdate = false;
            changed = true;
            logDetails += "Password diubah. ";
            passwordChanged = true;
        }

        if (newProfilePicturePath != null) { 
            String pathToSaveToDb = newProfilePicturePath.isEmpty() ? null : newProfilePicturePath;
            boolean pathIsDifferent = (user.getProfilePicturePath() == null && pathToSaveToDb != null) ||
                                      (user.getProfilePicturePath() != null && !user.getProfilePicturePath().equals(pathToSaveToDb)) ||
                                      (user.getProfilePicturePath() != null && pathToSaveToDb == null);
            if (pathIsDifferent) {
                if (!firstFieldToUpdate) sqlBuilder.append(", ");
                sqlBuilder.append("profile_picture_path = ?"); 
                params.add(pathToSaveToDb);
                changed = true;
                logDetails += (pathToSaveToDb == null) ? "Foto profil dihapus. " : "Foto profil diubah. ";
                pictureChanged = true;
            }
        }
        
        if (!changed) { 
            return true; 
        }

        sqlBuilder.append(" WHERE email = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            // ... (set parameter pstmt) ...
            int paramIndex = 1;
            for (Object param : params) {
                 if (param == null) {
                    pstmt.setNull(paramIndex++, Types.VARCHAR);
                } else {
                    pstmt.setObject(paramIndex++, param); 
                }
            }
            pstmt.setString(paramIndex, email); 
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // LOGGING: Update User Profile
                activityLogModel.logActivity(email, ActivityLogModel.ActionType.UPDATE_USER_PROFILE, logDetails.trim());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("AuthModel.updateUserProfile Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false; 
    }
}