package com.eco.EcoChain.security;

import com.eco.EcoChain.entity.Token;
import com.eco.EcoChain.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Autowired
    private TokenRepository tokenRepo;

    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        String token = authHeader.substring(7);

        Token tokenEntity = tokenRepo.findByAccessToken(token).orElse(null);
        if (tokenEntity != null){
            tokenEntity.setLoggedOut(true);
            tokenRepo.save(tokenEntity);
        }
    }
}
