package blockchain.Service;

import blockchain.Request.LoginRequest;
import blockchain.Request.RegisterRequest;
import blockchain.Response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void logout(String refreshToken);
    
    LoginResponse refresh(String refreshToken);
    
    LoginResponse refreshWithRotation(String oldRefreshToken, HttpServletResponse response);

	void register(RegisterRequest request);
}