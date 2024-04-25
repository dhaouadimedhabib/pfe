package com.example.pfe.Service;

import com.example.pfe.Domain.User;
import com.example.pfe.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

@Service

public class UserService {
    @Autowired
    UserRepo userRepo;

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
}
