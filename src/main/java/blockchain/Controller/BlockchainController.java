package blockchain.Controller;

import blockchain.Response.BlockchainIssueResponse;
import blockchain.Service.BlockchainService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blockchain/certificates")
public class BlockchainController {
    
	@Autowired
    private BlockchainService blockchainService;
    
    @PostMapping("/{certificateId}/issue")
    public ResponseEntity<BlockchainIssueResponse> issueCertificate(@PathVariable String certificateId) {
        return ResponseEntity.ok(blockchainService.issueCertificate(certificateId));
    }
    
    @PostMapping("/{certificateId}/revoke")
    public ResponseEntity<String> revokeCertificate(@PathVariable String certificateId) {
        String result = blockchainService.revokeCertificate(certificateId);
        return ResponseEntity.ok(result);
    }
}