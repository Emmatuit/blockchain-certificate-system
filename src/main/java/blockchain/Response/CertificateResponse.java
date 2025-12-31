package blockchain.Response;


public class CertificateResponse {
    public static class Builder {
        private String certificateId;
        private String status;
        private String studentName;
        private String course;
        private String issuedDate;

        public CertificateResponse build() {
            return new CertificateResponse(this);
        }

        public Builder certificateId(String certificateId) {
            this.certificateId = certificateId;
            return this;
        }

        public Builder course(String course) {
            this.course = course;
            return this;
        }

        public Builder issuedDate(String issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }
    }
    public static Builder builder() {
        return new Builder();
    }
    private String certificateId;
    private String status;
    private String studentName;

    private String course;

    private String issuedDate;
    public CertificateResponse() {}
    private CertificateResponse(Builder builder) {
        this.certificateId = builder.certificateId;
        this.status = builder.status;
        this.studentName = builder.studentName;
        this.course = builder.course;
        this.issuedDate = builder.issuedDate;
    }
    // Getters
    public String getCertificateId() { return certificateId; }
    public String getCourse() { return course; }

    public String getIssuedDate() { return issuedDate; }

    public String getStatus() { return status; }

    public String getStudentName() { return studentName; }
}