package blockchain.Service;

import blockchain.Request.CertificateRequest;
import blockchain.Response.CertificateResponse;

public interface CertificateService {
    CertificateResponse createCertificate(CertificateRequest request);
    String generateCertificateId();
    CertificateResponse getCertificate(String certificateId);
}