package blockchain.Entity;


import jakarta.persistence.*;
import java.time.LocalDate;

import blockchain.Enum.CertificateStatus;

@Entity
@Table(name = "certificates")
public class Certificate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String certificateId;
    
    @Column(nullable = false)
    private String studentName;
    
    @Column(nullable = false)
    private String course;
    
    @Column(nullable = false)
    private LocalDate issuedDate;
    
    private String hash;
    
    private String blockchainTxHash;
    
    @Enumerated(EnumType.STRING)
    private CertificateStatus status = CertificateStatus.CREATED;
    
    // Constructors
    public Certificate() {}
    
    public Certificate(String certificateId, String studentName, String course, 
                      LocalDate issuedDate, CertificateStatus status) {
        this.certificateId = certificateId;
        this.studentName = studentName;
        this.course = course;
        this.issuedDate = issuedDate;
        this.status = status;
    }
    
    public String getBlockchainTxHash() {
        return blockchainTxHash;
    }
    
    public String getCertificateId() {
        return certificateId;
    }
    
    public String getCourse() {
        return course;
    }
    
    public String getHash() {
        return hash;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public LocalDate getIssuedDate() {
        return issuedDate;
    }
    
    public CertificateStatus getStatus() {
        return status;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setBlockchainTxHash(String blockchainTxHash) {
        this.blockchainTxHash = blockchainTxHash;
    }
    
    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public void setHash(String hash) {
        this.hash = hash;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }
    
    public void setStatus(CertificateStatus status) {
        this.status = status;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}

