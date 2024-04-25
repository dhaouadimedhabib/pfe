package com.example.pfe.Service;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.RendezVous;
import com.example.pfe.Domain.User;
import com.example.pfe.Model.DisponibiliteDTO;
import com.example.pfe.Model.RendezVousDTO;
import com.example.pfe.Repo.DisponibiliteRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.RendezVousRepo;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RendezVousService {
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @Autowired
    DisponibiliteRepo disponibiliteRepo;

    @Autowired
    UserRepo userRepo;
    @Autowired
    RendezVousRepo rendezVousRepo;
    @Autowired
    DisponibiliteService disponibiliteService;
    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    private final JwtUtils jwtUtils;
    public static RendezVousDTO mapToDTO(final RendezVous rendezVous) {
        if (rendezVous == null) {
            return null;
        }

        RendezVousDTO rendezVousDTO = new RendezVousDTO();

        rendezVousDTO.setAppointmentId(rendezVous.getAppointmentId());
        rendezVousDTO.setDate(rendezVous.getDate());
        rendezVousDTO.setDebut(rendezVous.getDebut());
        rendezVousDTO.setFin(rendezVous.getFin());
        rendezVousDTO.setStatuts(rendezVous.getStatuts());
        if (rendezVous.getProfessional() != null) {
            rendezVousDTO.setProfessionalId(rendezVous.getProfessional().getIdProfessionnel());
        }
        if (rendezVous.getPaiement() != null) {
            rendezVousDTO.setPaiementId(rendezVous.getPaiement().getPaiementId());
        }
        if (rendezVous.getCommentaire() != null) {
            rendezVousDTO.setCommentaireId(rendezVous.getCommentaire().getCommentaireId());
        }

        return rendezVousDTO;
    }
/*
    public List<RendezVousDTO> findAllRendezVousByProfessionnel(String token) {
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

        // Récupérer les rendez-vous associés à ce professionnel
        List<RendezVous> rendezVousList = rendezVousRepo.findAllByProfessional(professionnel);

        // Mapper les rendez-vous en DTO
        return rendezVousList.stream()
                .map(rendezVous -> mapToDTO(rendezVous)) // Modification ici
                .collect(Collectors.toList());
    }

 */

    public RendezVousService(RendezVousRepo rendezVousRepo, JwtUtils jwtUtils) {
        this.rendezVousRepo = rendezVousRepo;
        this.jwtUtils = jwtUtils;
    }
    public List<RendezVousDTO> findAllRendezVousByProfessionnel(String token) {
        // Utiliser getProfessionnelFromJwtToken pour récupérer le professionnel
        Professionnel professionnel = jwtUtils.getProfessionnelFromJwtToken(token);

        if (professionnel == null) {
            // Si aucun professionnel n'est trouvé, retourner une liste vide
            System.out.println("Erreur : Professionnel non trouvé.");
            return Collections.emptyList();
        }

        // Récupérer les rendez-vous associés à ce professionnel
        List<RendezVous> rendezVousList = rendezVousRepo.findAllByProfessional(professionnel);

        // Mapper les rendez-vous en DTO
        return rendezVousList.stream()
                .map(rendezVous -> mapToDTO(rendezVous)) // Modification ici
                .collect(Collectors.toList());
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }



    public boolean ajouterRendezVous(String token, RendezVousDTO nouveauRendezVousDTO) {
        // Etape 1: Identifier le professionnel à partir du token JWT
        String username = getUserNameFromJwtToken(token);
        User user = userRepo.findByUsername(username);
        if (user == null || user.getProfessionnel() == null) {
            System.out.println("Erreur : Utilisateur ou Professionnel non trouvé.");
            return false;
        }
        Professionnel professionnel = user.getProfessionnel();

        // Etape 2: Vérifier la disponibilité du créneau
        List<Disponibilite> disponibilites = disponibiliteRepo.findAllByProfessionnel(professionnel);
        boolean estDansDisponibilite = disponibilites.stream().anyMatch(disponibilite ->
                nouveauRendezVousDTO.getDate().isEqual(disponibilite.getDate()) &&
                        !nouveauRendezVousDTO.getDebut().isBefore(disponibilite.getHeureDebut()) &&
                        !nouveauRendezVousDTO.getFin().isAfter(disponibilite.getHeureFin())
        );

        if (!estDansDisponibilite) {
            System.out.println("Le créneau demandé n'est pas disponible dans les disponibilités du professionnel.");
            return false;
        }

        // Etape 3: Vérifier les chevauchements de rendez-vous
        List<RendezVous> rendezVousExistants = rendezVousRepo.findAllByProfessional(professionnel);
        boolean estCreneauLibre = rendezVousExistants.stream().noneMatch(rendezVous ->
                // Vérifier tous les cas de chevauchement
                !nouveauRendezVousDTO.getFin().isBefore(rendezVous.getDebut()) && !nouveauRendezVousDTO.getDebut().isAfter(rendezVous.getFin())
        );

        if (!estCreneauLibre) {
            System.out.println("Le créneau n'est pas libre, il y a un chevauchement avec un autre rendez-vous.");
            return false;
        }

        // Etape 4: Sauvegarder le nouveau rendez-vous
        RendezVous nouveauRendezVous = new RendezVous();
        // Mapper nouveauRendezVousDTO à nouveauRendezVous ici, en incluant le professionnel
        nouveauRendezVous.setDate(nouveauRendezVousDTO.getDate());
        nouveauRendezVous.setDebut(nouveauRendezVousDTO.getDebut());
        nouveauRendezVous.setFin(nouveauRendezVousDTO.getFin());
        nouveauRendezVous.setStatuts(nouveauRendezVousDTO.getStatuts());
        nouveauRendezVous.setProfessional(professionnel);
        rendezVousRepo.save(nouveauRendezVous);

        return true;
    }




    public List<RendezVousDTO> findAllRendezVous(String token) {
        // Vérifier la validité du token
        if (!jwtUtils.validateJwtToken(token)) {
            System.out.println("Erreur : Token JWT invalide.");
            return Collections.emptyList();
        }

        List<RendezVous> list = rendezVousRepo.findAll();
        return list.stream()
                .map(rendezVous -> mapToDTO(rendezVous)) // Modification ici
                .collect(Collectors.toList());
    }


}
