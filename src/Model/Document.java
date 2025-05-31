package Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Document {
    private int documentId;
    private String name;
    private String ownerEmail;
    private String timestamp;
    private String filePath;
    private String fileType;
    private int fileSize;
    private boolean isDeleted;

    public static class AccessDetail {
        public String accessType;
        public String timestampGiven;

        public AccessDetail(String accessType, String timestampGiven) {
            this.accessType = accessType;
            this.timestampGiven = timestampGiven;
        }
    }

    private Map<String, AccessDetail> accessListWithTimestamp;

    public Document(String name, String ownerEmail, String timestamp, String filePath) {
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.timestamp = timestamp;
        this.filePath = filePath;
        this.fileType = getMimeTypeFromPath(filePath);
        this.accessListWithTimestamp = new HashMap<>();
        this.isDeleted = false;
        if (ownerEmail != null && !ownerEmail.isEmpty()) {
            this.accessListWithTimestamp.put(ownerEmail,
                    new AccessDetail("owner", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        }
    }
    
    public Document(int documentId, String name, String ownerEmail, String timestamp, 
                    String filePath, String fileType, int fileSize, 
                    boolean isDeleted) {
        this.documentId = documentId;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.timestamp = timestamp;
        this.filePath = filePath;
        this.fileType = fileType != null ? fileType : getMimeTypeFromPath(filePath);
        this.fileSize = fileSize;
        this.isDeleted = isDeleted;
        this.accessListWithTimestamp = new HashMap<>();
    }

    public int getDocumentId() { return documentId; }
    public String getName() { return name; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getTimestamp() { return timestamp; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public int getFileSize() { return fileSize; }
    public boolean isDeleted() { return isDeleted; }
    public Map<String, AccessDetail> getAccessListWithTimestamp() {
        return new HashMap<>(accessListWithTimestamp);
    }

    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setName(String name) { this.name = name; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        if (filePath != null) {
            this.fileType = getMimeTypeFromPath(filePath);
        } else {
            this.fileType = null;
        }
    }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }
    
    public void setAccessListWithTimestamp(Map<String, AccessDetail> accessList) {
        this.accessListWithTimestamp = (accessList != null) ? new HashMap<>(accessList) : new HashMap<>();
    }

    public boolean addAccess(String email, String accessType, String timestamp) {
        if (email == null || email.trim().isEmpty() ||
            accessType == null || accessType.trim().isEmpty() ||
            timestamp == null || timestamp.trim().isEmpty()) {
            return false;
        }
        this.accessListWithTimestamp.put(email.trim(), new AccessDetail(accessType, timestamp));
        return true;
    }

    public boolean revokeAccess(String email) {
        if (email != null && this.accessListWithTimestamp.containsKey(email.trim())) {
            this.accessListWithTimestamp.remove(email.trim());
            return true;
        }
        return false;
    }

    public boolean hasAccess(String email, String requiredAccessType) {
        if (email == null || requiredAccessType == null) return false;
        AccessDetail userAccessDetail = this.accessListWithTimestamp.get(email.trim());
        if (userAccessDetail != null) {
            String userAccessType = userAccessDetail.accessType;
            if (userAccessType.equals("owner")) return true;
            if (userAccessType.equals(requiredAccessType)) return true;
            if (requiredAccessType.equals("read") && userAccessType.equals("read-write")) return true;
        }
        return false;
    }

    private String getMimeTypeFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "application/octet-stream";
        }
        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0 && i < filePath.length() - 1) {
            extension = filePath.substring(i + 1).toLowerCase();
        }
        switch (extension) {
            case "txt": return "text/plain";
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            default: return "application/octet-stream";
        }
    }
}