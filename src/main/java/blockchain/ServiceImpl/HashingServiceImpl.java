package blockchain.ServiceImpl;

import blockchain.Service.HashingService;
import blockchain.Util.HashUtil;

import org.springframework.stereotype.Service;

@Service
public class HashingServiceImpl implements HashingService {

    @Override
    public String generateHash(String certificateId, String studentName, String course, String issuedDate) {
        return HashUtil.generateCertificateHash(certificateId, studentName, course, issuedDate);
    }

    @Override
    public boolean verifyHash(String certificateId, String studentName, String course, String issuedDate, String hash) {
        String generatedHash = generateHash(certificateId, studentName, course, issuedDate);
        return generatedHash.equals(hash);
    }
}