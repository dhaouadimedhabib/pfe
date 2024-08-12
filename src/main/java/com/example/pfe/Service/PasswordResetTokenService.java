package com.example.pfe.Service;

import com.example.pfe.Domain.PasswordResetToken;
import com.example.pfe.Domain.User;
import com.example.pfe.Repo.PasswordTokenRepository;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetTokenService {
    @Autowired
    private PasswordTokenRepository tokenRepository;

    @Autowired
    private UserRepo userRepository;

    public String createToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60)); // 1 hour expiry
        tokenRepository.save(resetToken);

        return token;
    }

    public User validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            throw new InvalidTokenException("Invalid or expired token");
        }
        return resetToken.getUser();
    }

}
