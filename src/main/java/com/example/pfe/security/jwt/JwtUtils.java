package com.example.pfe.security.jwt;

import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.User;
import com.example.pfe.Service.UserService;
import com.example.pfe.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Autowired
    private UserService userService;
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setSubject(String.valueOf(userPrincipal.getId())) // Convertir l'ID en chaîne
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }


    public User getUserFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject();
            Long userId = Long.parseLong(userIdStr); // Convertir le subject (String) en Long
            return userService.findById(userId);
        } catch (NumberFormatException e) {
            logger.error("Subject cannot be converted to Long: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Could not extract user from token: {}", e.getMessage());
            return null;
        }
    }


    public Professionnel getProfessionnelFromJwtToken(String token) {
        Professionnel professionnel = null;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject();
            Long userId = Long.parseLong(userIdStr); // Convertir le subject en Long
            User user = userService.findById(userId);

            if (user != null) {
                professionnel = user.getProfessionnel();
                if (professionnel == null) {
                    logger.error("No Professionnel associated with user ID: {}", userId);
                }
            } else {
                logger.error("User not found with ID: {}", userId);
            }
        } catch (NumberFormatException e) {
            logger.error("Subject cannot be converted to Long: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Could not extract user from token: {}", e.getMessage());
        }
        return professionnel; // Cette méthode retourne null si aucun Professionnel n'est associé ou en cas d'erreur
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
