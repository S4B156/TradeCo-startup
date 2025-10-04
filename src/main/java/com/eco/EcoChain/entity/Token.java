package com.eco.EcoChain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    Long tokenId;

    @Column(name = "access_token")
    String accessToken;

    @Column(name = "refresh_token")
    String refreshToken;

    @Column(name = "logged_out")
    boolean loggedOut;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
