package Model;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AuthModel {
    private Map<String, String> users; // Untuk menyimpan kredensial pengguna
    private Map<String, Document> documents; // Untuk menyimpan dokumen

    public AuthModel() {
        users = new HashMap<>();
        documents = new HashMap<>();
        users.put("admin", "password123"); // Contoh kredensial awal
    }

    // Metode autentikasi
    public boolean authenticate(String username, String password) {
        if (users.containsKey(username)) {
            return users.get(username).equals(password);
        }
        return false;
    }

    // Metode registrasi
    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, password);
        return true;
    }

    // Metode untuk menambahkan dokumen
    public boolean addDocument(String name, String email, String timestamp, String filePath) {
        System.out.println("Mencoba menambahkan dokumen: name=" + name + ", email=" + email + ", timestamp=" + timestamp + ", filePath=" + filePath);
        if (name == null || name.trim().isEmpty() || email == null || timestamp == null || filePath == null) {
            System.out.println("Gagal: Parameter tidak valid");
            return false;
        }
        if (documents.containsKey(name)) {
            System.out.println("Gagal: Nama dokumen sudah ada");
            return false;
        }
        Document doc = new Document(name, email, timestamp, filePath);
        documents.put(name, doc);
        System.out.println("Berhasil menambahkan dokumen. Jumlah dokumen: " + documents.size());
        return true;
    }

    // Metode untuk menghapus dokumen
    public boolean removeDocument(String name) {
        if (name == null || !documents.containsKey(name)) {
            return false;
        }
        documents.remove(name);
        return true;
    }

    // Metode untuk memperbarui dokumen (nama dan isi file)
    public boolean updateDocument(String oldName, String newName, String email, String timestamp, String newContent) {
        if (oldName == null || !documents.containsKey(oldName) || email == null || timestamp == null || newContent == null) {
            System.out.println("Gagal memperbarui: Parameter tidak valid atau dokumen tidak ditemukan");
            return false;
        }
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("Gagal memperbarui: Nama baru tidak valid");
            return false;
        }

        // Jika nama baru berbeda dan sudah ada di dokumen lain, tolak pembaruan
        if (!oldName.equals(newName) && documents.containsKey(newName)) {
            System.out.println("Gagal memperbarui: Nama baru sudah digunakan");
            return false;
        }

        // Ambil dokumen lama
        Document oldDoc = documents.get(oldName);
        String filePath = oldDoc.getFilePath();

        try {
            // Tulis konten baru ke file yang sama
            Files.write(Paths.get(filePath), newContent.getBytes());
            System.out.println("Berhasil menulis konten baru ke: " + filePath);
        } catch (IOException e) {
            System.out.println("Gagal menulis ke file: " + e.getMessage());
            return false;
        }

        // Buat dokumen baru dengan data yang diperbarui
        Document newDoc = new Document(newName, email, timestamp, filePath);
        // Hapus entri lama jika nama berubah
        if (!oldName.equals(newName)) {
            documents.remove(oldName);
        }
        // Tambahkan entri baru
        documents.put(newName, newDoc);
        System.out.println("Berhasil memperbarui dokumen: " + oldName + " menjadi " + newName);
        return true;
    }

    // Metode untuk mendapatkan daftar dokumen
    public Map<String, Document> getDocuments() {
        return new HashMap<>(documents); // Kembalikan salinan untuk keamanan
    }
}