package blockchain.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import blockchain.Entity.RefreshToken;
import blockchain.Repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private static final long EXPIRY_DAYS = 7;

    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public String createRefreshToken(String email) {
        String token = UUID.randomUUID().toString();

        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setEmail(email);
        rt.setExpiry(Instant.now().plus(EXPIRY_DAYS, ChronoUnit.DAYS));

        repo.save(rt);
        return token;
    }

    public void revoke(String token) {
        repo.deleteByToken(token);
    }

    public String validateAndGetEmail(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (rt.getExpiry().isBefore(Instant.now())) {
            repo.delete(rt);
            throw new RuntimeException("Refresh token expired");
        }
        return rt.getEmail();
    }
    
    
    
}
