package blockchain.Response;

public class BlockchainIssueResponse {
    public static class Builder {
        private String status;
        private String transactionHash;

        public BlockchainIssueResponse build() {
            return new BlockchainIssueResponse(this);
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder transactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
            return this;
        }
    }
    public static Builder builder() {
        return new Builder();
    }

    private String status;

    private String transactionHash;
    public BlockchainIssueResponse() {}

    private BlockchainIssueResponse(Builder builder) {
        this.status = builder.status;
        this.transactionHash = builder.transactionHash;
    }

    public String getStatus() { return status; }

    public String getTransactionHash() { return transactionHash; }
}