package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PORT = "3306";
    private static final String ADMIN_DB_USER = "root"; 
    private static final String ADMIN_DB_PASSWORD = ""; 

    private static final String APP_DB_NAME = "document_management_app_db";

    private static final String APP_DB_OPERATIONAL_USER = "app_doc_user"; 
    private static final String APP_DB_OPERATIONAL_PASSWORD = "app_doc_secure_password123"; 

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL_MYSQL_SERVER = "jdbc:mysql://" + SERVER_HOST + ":" + SERVER_PORT + "/?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String JDBC_URL_APP_DATABASE = "jdbc:mysql://" + SERVER_HOST + ":" + SERVER_PORT + "/" + APP_DB_NAME + "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private static Connection applicationConnection = null;

    public static void initializeDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            ensureDatabaseExists();
            ensureTablesExist();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver tidak ditemukan.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Gagal inisialisasi struktur database.", e);
        }
    }

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection serverConn = DriverManager.getConnection(JDBC_URL_MYSQL_SERVER, ADMIN_DB_USER, ADMIN_DB_PASSWORD);
             Statement stmt = serverConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + APP_DB_NAME + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
    }
    
    @SuppressWarnings("unused")
    private static void ensureAppUserExistsAndHasPermissions() throws SQLException {
         try (Connection serverConn = DriverManager.getConnection(JDBC_URL_MYSQL_SERVER, ADMIN_DB_USER, ADMIN_DB_PASSWORD);
             Statement stmt = serverConn.createStatement()) {
            try {
                stmt.executeUpdate("CREATE USER IF NOT EXISTS '" + APP_DB_OPERATIONAL_USER + "'@'localhost' IDENTIFIED BY '" + APP_DB_OPERATIONAL_PASSWORD + "'");
            } catch (SQLException eCreateUser) {
                if (!(eCreateUser.getErrorCode() == 1396 || eCreateUser.getMessage().toLowerCase().contains("already exists"))) {
                    System.err.println("Peringatan: Gagal membuat pengguna aplikasi '" + APP_DB_OPERATIONAL_USER + "'. Pesan: " + eCreateUser.getMessage());
                }
            }
            stmt.executeUpdate("GRANT SELECT, INSERT, UPDATE, DELETE ON `" + APP_DB_NAME + "`.* TO '" + APP_DB_OPERATIONAL_USER + "'@'localhost'");
            stmt.executeUpdate("FLUSH PRIVILEGES");
        }
    }

    private static void ensureTablesExist() throws SQLException {
        try (Connection dbConn = DriverManager.getConnection(JDBC_URL_APP_DATABASE, ADMIN_DB_USER, ADMIN_DB_PASSWORD);
             Statement stmt = dbConn.createStatement()) {

            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS `users` ("
                    + "`user_id` INT AUTO_INCREMENT PRIMARY KEY,"
                    + "`username` VARCHAR(100) NOT NULL UNIQUE,"
                    + "`email` VARCHAR(150) NOT NULL UNIQUE,"
                    + "`password_hash` VARCHAR(255) NOT NULL,"
                    + "`profile_picture_blob` MEDIUMBLOB NULL,"
                    + "`profile_picture_type` VARCHAR(100) NULL,"
                    + "`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createUsersTableSQL);

            String createDocumentsTableSQL = "CREATE TABLE IF NOT EXISTS `documents` ("
                    + "`document_id` INT AUTO_INCREMENT PRIMARY KEY,"
                    + "`owner_user_id` INT NOT NULL,"
                    + "`document_name` VARCHAR(255) NOT NULL,"
                    + "`file_path` VARCHAR(1024) NULL," 
                    + "`timestamp_created` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "`timestamp_modified` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "`is_deleted` BOOLEAN DEFAULT FALSE,"
                    + "`deleted_timestamp` TIMESTAMP NULL,"
                    + "FOREIGN KEY (`owner_user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createDocumentsTableSQL);

            String createDocAccessTableSQL = "CREATE TABLE IF NOT EXISTS `document_access` ("
                    + "`access_id` INT AUTO_INCREMENT PRIMARY KEY,"
                    + "`document_id` INT NOT NULL,"
                    + "`user_id_shared_with` INT NOT NULL,"
                    + "`access_type` VARCHAR(50) NOT NULL," 
                    + "`timestamp_granted` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (`document_id`) REFERENCES `documents`(`document_id`) ON DELETE CASCADE,"
                    + "FOREIGN KEY (`user_id_shared_with`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,"
                    + "UNIQUE KEY `unique_access` (`document_id`, `user_id_shared_with`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createDocAccessTableSQL);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (applicationConnection == null || applicationConnection.isClosed()) {
            try {
                Class.forName(JDBC_DRIVER); 
                applicationConnection = DriverManager.getConnection(JDBC_URL_APP_DATABASE, APP_DB_OPERATIONAL_USER, APP_DB_OPERATIONAL_PASSWORD);
            } catch (SQLException eOpUser) {
                applicationConnection = DriverManager.getConnection(JDBC_URL_APP_DATABASE, ADMIN_DB_USER, ADMIN_DB_PASSWORD);
            } catch (ClassNotFoundException eDriver) {
                 throw new SQLException("MySQL JDBC Driver tidak ditemukan.", eDriver);
            }
        }
        return applicationConnection;
    }

    public static void closeConnection() {
        try {
            if (applicationConnection != null && !applicationConnection.isClosed()) {
                applicationConnection.close();
                applicationConnection = null; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}