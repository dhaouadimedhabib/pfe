package com.example.pfe.Service;

import com.example.pfe.Domain.PasswordResetToken;
import com.example.pfe.Domain.Role;
import com.example.pfe.Domain.RoleName;
import com.example.pfe.Domain.User;
import com.example.pfe.Repo.*;
import io.opencensus.internal.DefaultVisibilityForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;


    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(Long id){
        return userRepo.findById(id).get();
    }
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
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
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<User> getProfessionalsByServiceName(String serviceName) {
        System.out.println("Service Name: " + serviceName);
        return userRepo.findProfessionalsByServiceName(serviceName);

    }


}
