package com.eco.EcoChain.controller;

import com.eco.EcoChain.dto.AuthenticationResponseDto;
import com.eco.EcoChain.dto.LoginRequestDto;
import com.eco.EcoChain.dto.RegistrationRequestDto;
import com.eco.EcoChain.dto.TokenResponseDto;
import com.eco.EcoChain.service.AuthenticationService;
import com.eco.EcoChain.service.JwtService;
import com.eco.EcoChain.service.objects.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody RegistrationRequestDto registrationRequest){
        if(userService.existsByUsername(registrationRequest.getUsername())){
            return ResponseEntity.badRequest().body("The username is already taken");
        }
        if(userService.existsByEmail(registrationRequest.getEmail())){
            return ResponseEntity.badRequest().body("The email is already taken");
        }
        authenticationService.register(registrationRequest);

        return ResponseEntity.ok("Registration was successful");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequest,
                                                  HttpServletResponse response){
        AuthenticationResponseDto authResponse = authenticationService.authenticate(loginRequest);

        Cookie refreshCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/refresh_token");
        refreshCookie.setMaxAge((int) (jwtService.getRefreshTokenExpiration() / 1000));
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new TokenResponseDto(authResponse.getAccessToken()));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<TokenResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        return authenticationService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    authenticationService.revokeToken(cookie.getValue());
                    cookie.setMaxAge(0);
                    cookie.setPath("/api/refresh_token");
                    response.addCookie(cookie);
                }
            }
        }
        return ResponseEntity.ok("Logged out");
    }

}