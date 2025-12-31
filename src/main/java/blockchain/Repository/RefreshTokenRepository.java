package blockchain.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

import blockchain.Entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    void deleteAllByEmail(String email);

    void deleteByToken(String token);

    Optional<RefreshToken> findByToken(String token);
}
