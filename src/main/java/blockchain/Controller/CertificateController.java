package blockchain.Controller;

import blockchain.Request.CertificateRequest;
import blockchain.Response.CertificateResponse;
import blockchain.Service.CertificateService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/certificates")

public class CertificateController {
    
	@Autowired
    private CertificateService certificateService;
    
    @PostMapping
    public ResponseEntity<CertificateResponse> createCertificate(@RequestBody CertificateRequest request) {
        return ResponseEntity.ok(certificateService.createCertificate(request));
    }
    
    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateResponse> getCertificate(@PathVariable String certificateId) {
        return ResponseEntity.ok(certificateService.getCertificate(certificateId));
    }
}