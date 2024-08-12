package com.example.pfe.Repo;

import com.example.pfe.Domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

}