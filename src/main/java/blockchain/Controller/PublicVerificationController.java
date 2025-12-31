package blockchain.Controller;

import blockchain.Response.VerificationResponse;
import blockchain.ServiceImpl.BlockchainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/verify")
public class PublicVerificationController {
    
    @Autowired
    private BlockchainServiceImpl blockchainService;
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        boolean connected = blockchainService.isBlockchainConnected();
        return connected ? 
            ResponseEntity.ok("✅ Blockchain connected") :
            ResponseEntity.status(503).body("❌ Blockchain not connected");
    }
    
    @GetMapping("/{certificateId}")
    public ResponseEntity<VerificationResponse> verifyCertificate(@PathVariable String certificateId) {
        VerificationResponse response = blockchainService.getCertificateVerificationDetails(certificateId);
        return ResponseEntity.ok(response);
    }
}