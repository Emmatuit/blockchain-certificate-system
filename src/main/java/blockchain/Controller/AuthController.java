package blockchain.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import blockchain.Request.LoginRequest;
import blockchain.Response.LoginResponse;
import blockchain.Service.AuthService;
import blockchain.Service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
	@Autowired
    private AuthService authService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
    
    
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request);

        Cookie refreshCookie = new Cookie(
                "refreshToken",
                refreshTokenService.createRefreshToken(request.getEmail())
        );

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // HTTPS ONLY
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        response.addCookie(refreshCookie);

        return ResponseEntity.ok(loginResponse);
    }

    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {

        refreshTokenService.revoke(refreshToken);

        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/auth/refresh");
        deleteCookie.setMaxAge(0);

        response.addCookie(deleteCookie);

        return ResponseEntity.noContent().build();
    }

    
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {

        LoginResponse newTokens =
                authService.refreshWithRotation(refreshToken, response);

        return ResponseEntity.ok(newTokens);
    }


}