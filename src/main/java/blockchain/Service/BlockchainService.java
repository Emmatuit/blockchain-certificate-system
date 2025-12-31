package blockchain.Service;

import blockchain.Response.BlockchainIssueResponse;

public interface BlockchainService {
	
    BlockchainIssueResponse issueCertificate(String certificateId);
    String revokeCertificate(String certificateId);
    boolean verifyCertificateOnChain(String certificateId);
}