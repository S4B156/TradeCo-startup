package com.eco.EcoChain.service;

import com.eco.EcoChain.dto.AuthenticationResponseDto;
import com.eco.EcoChain.dto.LoginRequestDto;
import com.eco.EcoChain.dto.RegistrationRequestDto;
import com.eco.EcoChain.dto.TokenResponseDto;
import com.eco.EcoChain.entity.Token;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.enums.Role;
import com.eco.EcoChain.repository.TokenRepository;
import com.eco.EcoChain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(PasswordEncoder passwordEncoder, UserRepository userRepository, TokenRepository tokenRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public void register(RegistrationRequestDto registerForm){
        User user = new User();

        user.setUsername(registerForm.getUsername());
        user.setEmail(registerForm.getEmail());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    private void revokeAllToken(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokenByUser(user.getUserId());

        if(!validTokens.isEmpty()){
            validTokens.forEach(t ->{
                t.setLoggedOut(true);
            });
        }
        tokenRepository.saveAll(validTokens);
    }

    public void revokeToken(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(token -> {
                    token.setLoggedOut(true);
                    tokenRepository.save(token);
                });
    }

    private void saveUserToken(String accessToken, String refreshToken, User user){
        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }

    public AuthenticationResponseDto authenticate(LoginRequestDto loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllToken(user);

        saveUserToken(accessToken, refreshToken, user);
        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    public ResponseEntity<TokenResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtService.isValidRefresh(refreshToken, user)) {
            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);
            saveUserToken(newAccessToken, newRefreshToken, user);

            Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/api/refresh_token");
            refreshCookie.setMaxAge((int) (jwtService.getRefreshTokenExpiration() / 1000));
            response.addCookie(refreshCookie);

            return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}