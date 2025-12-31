package blockchain.Request;



import java.time.LocalDate;


public class CertificateRequest {
    private String studentName;
    private String course;
    private LocalDate issuedDate;

    public String getCourse() { return course; }
    public LocalDate getIssuedDate() { return issuedDate; }
    
    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setCourse(String course) { this.course = course; }
    
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}