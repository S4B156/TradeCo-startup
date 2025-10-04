package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("A user with the username " + username + " not found"));
    }
    public boolean existsByUsername(String username){ return userRepo.existsByUsername(username); }
    public boolean existsByEmail(String email){ return userRepo.existsByEmail(email); }
}
