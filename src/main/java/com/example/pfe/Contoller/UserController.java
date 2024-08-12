package com.example.pfe.Contoller;

import com.example.pfe.Domain.Role;
import com.example.pfe.Domain.RoleName;
import com.example.pfe.Domain.User;
import com.example.pfe.Repo.RoleRepo;
import com.example.pfe.Service.UserService;
import com.example.pfe.exception.ResourceNotFoundException;
import com.example.pfe.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Service
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")

public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepo roleRepo;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        boolean isUpdated = userService.updateUser(user);
        if (isUpdated) {
            return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found or could not be deleted!"));
        }
    }
    @GetMapping("/professionnels")
    public ResponseEntity<List<User>> getAllProfessionnels() {
        List<User> professionnels = userService.getAllProfessionnels();
        return ResponseEntity.ok(professionnels);
    }

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/professionals/filter")
    public ResponseEntity<List<User>> getProfessionalsByServiceName(@RequestParam("serviceName") String serviceName) {
        List<User> professionals = userService.getProfessionalsByServiceName(serviceName);
        if (professionals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(professionals);
    }



}


