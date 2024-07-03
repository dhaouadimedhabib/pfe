package com.example.pfe.Contoller;

import com.example.pfe.Domain.*;
import com.example.pfe.Repo.ClientRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.RoleRepo;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.payload.request.LoginRequest;
import com.example.pfe.payload.request.SignupRequest;
import com.example.pfe.payload.response.JwtResponse;
import com.example.pfe.payload.response.MessageResponse;
import com.example.pfe.security.jwt.JwtUtils;
import com.example.pfe.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleRepo roleRepo;
    @Autowired(required=false)
    PasswordEncoder encoder;
    @Autowired(required=false)
    JwtUtils jwtUtils;
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @Autowired
    ClientRepo clientRepo;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getIdProfessionnel(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        boolean isProfessional = true;
        boolean isClient =true;

        if (userRepo.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        Set<String> strRoles = signUpRequest.getRole();
        user.setEmail(signUpRequest.getEmail());
        user.setNumeroTel(signUpRequest.getNumeroTel());
        user.setImage(signUpRequest.getImage());
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role defaultRole = roleRepo.findByRoleName(RoleName.CLIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Default role not found!"));
            roles.add(defaultRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleRepo.findByRoleName(RoleName.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Admin role not found!"));
                        roles.add(adminRole);
                        break;
                    case "PROFESSIONAL":
                        Role professionalRole = roleRepo.findByRoleName(RoleName.PROFESSIONAL)
                                .orElseThrow(() -> new RuntimeException("Error: Professional role not found!"));
                        roles.add(professionalRole);
                        break;
                    case "CLIENT":
                        Role clientRole = roleRepo.findByRoleName(RoleName.CLIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Client role not found!"));
                        roles.add(clientRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Invalid role!");
                }
            });
        }
        user.setRoles(roles);
        User savedUser = userRepo.save(user);

        if (strRoles.contains("PROFESSIONAL")) {
            Professionnel professionnel = new Professionnel();
            // Set any other attributes for the professional
            professionnel.setUser(savedUser); // Associate the user with the professional
            professionnelRepo.save(professionnel);
            user.setProfessionnel(professionnel);
        }

        // Check if the user is a client
        if (strRoles.contains("CLIENT")) {
            Client client = new Client();
            // Set any other attributes for the client
            client.setUser(savedUser); // Associate the user with the client
            clientRepo.save(client);
            user.setClient(client);
        }
        userRepo.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


}
