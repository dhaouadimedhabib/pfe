package com.example.pfe.Service;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.User;
import com.example.pfe.Model.DisponibiliteDTO;
import com.example.pfe.Repo.DisponibiliteRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DisponibiliteService {
    @Autowired
    DisponibiliteRepo disponibiliteRepo;

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProfessionnelRepo professionnelRepo;

    private DisponibiliteDTO mapToDTO(final Disponibilite disponibilite, final DisponibiliteDTO disponibiliteDTO) {
        disponibiliteDTO.setIdDisponibilite(disponibilite.getIdDisponibilite());
        disponibiliteDTO.setJour(disponibilite.getJour());
        disponibiliteDTO.setHeureDebut(disponibilite.getHeureDebut());
        disponibiliteDTO.setHeureFin(disponibilite.getHeureFin());
        disponibiliteDTO.setDate(disponibilite.getDate());
        // Supposant que tu veux également mapper l'ID du professionnel associé à cette disponibilité
        disponibiliteDTO.setProfessionnelId(disponibilite.getProfessionnel() != null ? disponibilite.getProfessionnel().getIdProfessionnel() : null);

        return disponibiliteDTO;
    }

    public List<DisponibiliteDTO> findAllDisponibilites() {
        final List<Disponibilite> disponibilites = disponibiliteRepo.findAll();
        return disponibilites.stream()
                .map(disponibilite -> mapToDTO(disponibilite, new DisponibiliteDTO()))
                .collect(Collectors.toList());
    }
/*
    //Récupérer toutes les disponibilités de chaque professionnel
    public List<DisponibiliteDTO> findAllByProfessionnelDTO(String token) {
        // Récupérer le nom d'utilisateur à partir du jeton JWT
        String username = getUserNameFromJwtToken(token);

        // Rechercher l'utilisateur correspondant dans la base de données par son nom d'utilisateur
        User user = userRepo.findByUsername(username);
        if (user == null) {
            System.out.println("Erreur : Utilisateur non trouvé.");
            return Collections.emptyList();
        }

        // Récupérer le professionnel associé à l'utilisateur
        Professionnel professionnel = user.getProfessionnel();
        if (professionnel == null) {
            System.out.println("Erreur : Professionnel non trouvé.");
            return Collections.emptyList();
        }

        // Récupérer les disponibilités associées à ce professionnel
        List<Disponibilite> disponibilites = disponibiliteRepo.findAllByProfessionnel(professionnel);

        // Mapper les disponibilités en DTO
        return disponibilites.stream()
                .map(disponibilite -> mapToDTO(disponibilite, new DisponibiliteDTO()))
                .collect(Collectors.toList());
    }
*/
    public List<DisponibiliteDTO> findAllByProfessionnelDTO(Long idProfessionnel) {
        // Rechercher le professionnel par son identifiant
        Professionnel professionnel = professionnelRepo.findById(idProfessionnel).orElse(null);
        if (professionnel == null) {
            System.out.println("Erreur : Professionnel non trouvé.");
            return Collections.emptyList();
        }

        // Récupérer les disponibilités associées à ce professionnel
        List<Disponibilite> disponibilites = disponibiliteRepo.findAllByProfessionnel(professionnel);

        // Mapper les disponibilités en DTO
        return disponibilites.stream()
                .map(disponibilite -> mapToDTO(disponibilite, new DisponibiliteDTO()))
                .collect(Collectors.toList());
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

}
