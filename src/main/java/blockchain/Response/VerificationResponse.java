package blockchain.Response;



public class VerificationResponse {
    // Builder class
    public static class Builder {
        private boolean valid;
        private String issuer;
        private Long issuedAt;
        private boolean revoked;

        public VerificationResponse build() {
            return new VerificationResponse(this);
        }

        public Builder issuedAt(Long issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }
    }
    // Static builder method
    public static Builder builder() {
        return new Builder();
    }
    private boolean valid;
    private String issuer;

    private Long issuedAt;

    private boolean revoked;
    // Optional: No-args constructor if needed
    public VerificationResponse() {}
    // Private constructor for Builder
    private VerificationResponse(Builder builder) {
        this.valid = builder.valid;
        this.issuer = builder.issuer;
        this.issuedAt = builder.issuedAt;
        this.revoked = builder.revoked;
    }
    public Long getIssuedAt() { return issuedAt; }

    public String getIssuer() { return issuer; }

    public boolean isRevoked() { return revoked; }

    // Getters only (no setters for immutability)
    public boolean isValid() { return valid; }
}