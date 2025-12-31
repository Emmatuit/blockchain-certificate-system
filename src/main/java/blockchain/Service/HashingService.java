package blockchain.Service;

public interface HashingService {
    String generateHash(String certificateId, String studentName, String course, String issuedDate);
    boolean verifyHash(String certificateId, String studentName, String course, String issuedDate, String hash);
}