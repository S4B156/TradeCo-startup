package com.eco.EcoChain.entity;

import com.eco.EcoChain.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    String username;
    String email;
    String password;
    String companyName;
    String phone;
    String address;
    double rating;

    @OneToMany(mappedBy = "user1")
    private List<Chat> chatsAsUser1;

    @OneToMany(mappedBy = "user2")
    private List<Chat> chatsAsUser2;

    @Enumerated(EnumType.STRING)
    Role role;

    @Column(name = "created_at")
    LocalDateTime createdAt;
//    @OneToMany(mappedBy = "user")
//    List<Token> tokens;

    @OneToMany(mappedBy = "supplier")
    @JsonManagedReference
    List<Material> materials;

    @OneToMany(mappedBy = "requester")
    List<Request> requests;

    @OneToMany(mappedBy = "supplier")
    List<Deal> dealsAsSupplier;

    @OneToMany(mappedBy = "consumer")
    List<Deal> dealsAsConsumer;

    @Transient
    public List<Chat> getAllChats() {
        List<Chat> all = new ArrayList<>(chatsAsUser1);
        all.addAll(chatsAsUser2);
        return all;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
