package Model;

public class Document {
    private String name;
    private String email;
    private String timestamp;
    private String filePath;

    public Document(String name, String email, String timestamp, String filePath) {
        this.name = name;
        this.email = email;
        this.timestamp = timestamp;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}