package com.example.pfe.Service;

import com.example.pfe.Domain.RoleName;
import com.example.pfe.Domain.User;
import com.example.pfe.Repo.*;
import com.example.pfe.security.jwt.JwtUtils;
import com.example.pfe.security.services.UserDetailsServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfessionnelRepo professionnelRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ClientRepo clientRepo;
    @Autowired
    private JavaMailSender mailSender;
    private static final long EXPIRE_TOKEN=30;

    private  JwtUtils jwtUtil;
    private  UserDetailsServiceImpl userDetailsService;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(Long id){
        return userRepo.findById(id).get();
    }

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }
    public boolean updateUser(User updatedUser) {
        Optional<User> existingUserOptional = userRepo.findById(updatedUser.getUserId());
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Mettre à jour seulement les champs spécifiques
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setNumeroTel(updatedUser.getNumeroTel());
            existingUser.setImage(updatedUser.getImage());

            // Vérifiez si le mot de passe a changé avant de le crypter
            if (!passwordEncoder.matches(updatedUser.getPassword(), existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Enregistrez l'objet mis à jour sans toucher aux rôles
            userRepo.save(existingUser);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        try {
            if (userRepo.existsById(userId)) {
                // Supprimer les entrées dans Professionnel
                professionnelRepo.deleteByUserUserId(userId);
                // Supprimer les entrées dans Client
                clientRepo.deleteByUserUserId(userId);
                // Supprimer l'utilisateur
                userRepo.deleteById(userId);
                return true;
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            // Optionally rethrow the exception or handle it as needed
        }
        return false;
    }

    public List<User> getAllProfessionnels() {
        return userRepo.findUsersByRoleName(RoleName.PROFESSIONAL);
    }


    public void changePassword(User user, String newPassword) {
        user.setPassword(newPassword); // Ensure password is encoded before saving
        userRepo.save(user);
    }


    public List<User> getProfessionalsByServiceName(String serviceName) {
        System.out.println("Service Name: " + serviceName);
        return userRepo.findProfessionalsByServiceName(serviceName);

    }

    public User getUserByToken(HttpServletRequest request) {
        // Extraire le token du header Authorization
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = jwtUtil.getUserNameFromJwtToken(token);

            if (username != null) {
                // Rechercher l'utilisateur dans la base de données par son nom d'utilisateur
                return userRepo.findByUsername(username);
            }
        }

        throw new RuntimeException("Token is missing or invalid");
    }
    public String forgotPass(String email) {
        List<User> users = userRepo.findByEmail(email);

        if (users.isEmpty()) {
            return "Invalid email id.";
        } else if (users.size() > 1) {
            return "Multiple users found with the same email.";
        }

        User user = users.get(0);
        user.setToken(generateToken());
        user.setTokenCreationDate(LocalDateTime.now());

        user = userRepo.save(user);
        return user.getToken();
    }
    public String resetPass(String token, String password) {
        Optional<User> userOptional = Optional.ofNullable(userRepo.findByToken(token));

        if (!userOptional.isPresent()) {
            return "Invalid token";
        }

        LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();

        if (isTokenExpired(tokenCreationDate)) {
            return "Token expired.";
        }

        User user = userOptional.get();

        // Encoder le mot de passe avant de le sauvegarder
        user.setPassword(passwordEncoder.encode(password));
        user.setToken(null);
        user.setTokenCreationDate(null);

        userRepo.save(user);

        return "Your password successfully updated.";
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID().toString())
                .append(UUID.randomUUID().toString()).toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >=EXPIRE_TOKEN;
    }

}
