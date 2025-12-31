

package blockchain.ServiceImpl;

import blockchain.Entity.Certificate;
import blockchain.Enum.CertificateStatus;
import blockchain.Repository.CertificateRepository;
import blockchain.Request.CertificateRequest;
import blockchain.Response.CertificateResponse;
import blockchain.Service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CertificateRepository certificateRepository;

    @Override
    public CertificateResponse createCertificate(CertificateRequest request) {
        String certificateId = generateCertificateId();
        
        Certificate certificate = new Certificate();
        certificate.setCertificateId(certificateId);
        certificate.setStudentName(request.getStudentName());
        certificate.setCourse(request.getCourse());
        certificate.setIssuedDate(request.getIssuedDate());
        certificate.setStatus(CertificateStatus.CREATED);

        certificateRepository.save(certificate);

        return CertificateResponse.builder()
            .certificateId(certificateId)
            .status(certificate.getStatus().name())
            .studentName(certificate.getStudentName())
            .course(certificate.getCourse())
            .issuedDate(certificate.getIssuedDate().format(DATE_FORMATTER))
            .build();
    }

    @Override
    public String generateCertificateId() {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CERT-" + LocalDate.now().getYear() + "-" + uuid;
    }

    @Override
    public CertificateResponse getCertificate(String certificateId) {
        Optional<Certificate> certOpt = certificateRepository.findByCertificateId(certificateId);
        
        if (certOpt.isEmpty()) {
            throw new RuntimeException("Certificate not found");
        }

        Certificate certificate = certOpt.get();
        
        return CertificateResponse.builder()
            .certificateId(certificate.getCertificateId())
            .status(certificate.getStatus().name())
            .studentName(certificate.getStudentName())
            .course(certificate.getCourse())
            .issuedDate(certificate.getIssuedDate().format(DATE_FORMATTER))
            .build();
    }
}