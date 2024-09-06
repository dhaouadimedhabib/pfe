package com.example.pfe.Service;

import com.example.pfe.Domain.User;
import com.example.pfe.Repo.UserRepo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepository;

    @Autowired
    public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder, UserRepo userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void forgotPassword(@NonNull HttpServletRequest request, String password) {
        // Récupération de l'utilisateur à partir du token
        User user = userService.getUserByToken(request);

        // Hashage du mot de passe
        String hashedPassword = passwordEncoder.encode(password);

        // Mise à jour du mot de passe de l'utilisateur
        user.setPassword(hashedPassword);

        // Sauvegarde de l'utilisateur avec le nouveau mot de passe
        userRepository.save(user);
    }
}