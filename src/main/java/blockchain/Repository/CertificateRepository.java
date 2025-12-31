package blockchain.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import blockchain.Entity.Certificate;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    boolean existsByCertificateId(String certificateId);
    Optional<Certificate> findByCertificateId(String certificateId);
}