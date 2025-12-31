package blockchain.ServiceImpl;

import blockchain.Entity.User;
import blockchain.Enum.Role;
import blockchain.Repository.UserRepository;
import blockchain.Request.LoginRequest;
import blockchain.Request.RegisterRequest;
import blockchain.Response.LoginResponse;
import blockchain.Service.AuthService;
import blockchain.Service.RefreshTokenService;
import blockchain.Util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String accessToken =
                jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());

        refreshTokenService.createRefreshToken(user.getEmail()); // cookie handled in controller

        return LoginResponse.builder()
                .token(accessToken)
                .role(user.getRole().name())
                .build();
    }

  
    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }


    @Override
    public LoginResponse refresh(String refreshToken) {
        String email = refreshTokenService.validateAndGetEmail(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();

        String newAccessToken =
                jwtUtil.generateAccessToken(email, user.getRole().name());

        return LoginResponse.builder()
                .token(newAccessToken)
                .role(user.getRole().name())
                .build();
    }

    @Override
    public LoginResponse refreshWithRotation(
            String oldRefreshToken,
            HttpServletResponse response) {

        String email =
                refreshTokenService.validateAndGetEmail(oldRefreshToken);

        // 🔥 ROTATION: delete old token
        refreshTokenService.revoke(oldRefreshToken);

        // ✅ create new refresh token
        String newRefreshToken =
                refreshTokenService.createRefreshToken(email);

        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        User user = userRepository.findByEmail(email).orElseThrow();

        String newAccessToken =
                jwtUtil.generateAccessToken(email, user.getRole().name());

        return LoginResponse.builder()
                .token(newAccessToken)
                .role(user.getRole().name())
                .build();
    }
    
    
    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        userRepository.save(user);
    }

}
