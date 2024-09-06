package com.example.pfe.Contoller;

import com.example.pfe.Domain.*;
import com.example.pfe.Repo.ClientRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.RoleRepo;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.Service.*;
import com.example.pfe.payload.request.LoginRequest;
import com.example.pfe.payload.request.SignupRequest;
import com.example.pfe.payload.response.JwtResponse;
import com.example.pfe.payload.response.MessageResponse;
import com.example.pfe.security.jwt.JwtUtils;
import com.example.pfe.security.services.UserDetailsImpl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import javax.validation.Valid;
import com.google.api.client.googleapis.auth.oauth2.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;

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

    private  GoogleIdTokenVerifier tokenVerifier;
    private  GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    private  AuthenticationService authenticationService;

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







    public AuthController(GoogleIdTokenVerifier tokenVerifier,
                          GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow) {
        this.tokenVerifier = tokenVerifier;
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
    }
    @GetMapping("/token/{token}")
    @ResponseBody
    public boolean authenticateByJavascriptClientToken(@PathVariable String token) {
        // TODO add token exchange logic here!
        try {
            return tokenVerifier.verify(token).getPayload().getEmailVerified();
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/redirect")
    public RedirectView redirectToGoogle() {
        GoogleAuthorizationCodeRequestUrl authUrl =
                googleAuthorizationCodeFlow.newAuthorizationUrl();
        authUrl.setRedirectUri("http://localhost:8080/auth/callback");
        String url = authUrl.build();
        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public RedirectView authCallback(@RequestParam String code,
                                     HttpServletRequest servletRequest) throws IOException {
        GoogleAuthorizationCodeTokenRequest tokenRequest =
                googleAuthorizationCodeFlow.newTokenRequest(code);
        tokenRequest.setRedirectUri("http://localhost:8084/auth/callback");
        GoogleTokenResponse tokenResponse = tokenRequest.execute();
        GoogleIdToken token = tokenResponse.parseIdToken();
        token.getPayload().getEmail();

        // TODO replace session cookie with your your token exchange logic here
        setSessionCookie(servletRequest, tokenResponse);

        return new RedirectView("/");
    }

    private void setSessionCookie(HttpServletRequest request, GoogleTokenResponse tokenResponse) {

        // TODO Lookup credentials based on google token
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                "email", "credentials", new ArrayList<>());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

}
