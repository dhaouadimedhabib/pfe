package com.example.pfe.Service;

import com.example.pfe.Domain.*;
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

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        rendezVousDTO.setNomClient(rendezVous.getNomClient()); // Ajoutez cette ligne

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


    public List<RendezVousDTO> findAllRendezVousByProfessionnelId(Long professionnelId) {
        // Récupérer le professionnel à partir de son ID
        Professionnel professionnel = professionnelRepo.findById(professionnelId).orElse(null);

        if (professionnel == null) {
            // Si aucun professionnel n'est trouvé, retourner une liste vide
            System.out.println("Erreur : Professionnel non trouvé.");
            return Collections.emptyList();
        }

        // Récupérer les rendez-vous associés à ce professionnel
        List<RendezVous> rendezVousList = rendezVousRepo.findAllByProfessional(professionnel);

        // Mapper les rendez-vous en DTO
        return rendezVousList.stream()
                .map(rendezVous -> mapToDTO(rendezVous))
                .collect(Collectors.toList());
    }






    public List<RendezVousDTO> findAllRendezVous() {
        // Récupérer tous les rendez-vous de la base de données
        List<RendezVous> list = rendezVousRepo.findAll();

        // Transformer les objets RendezVous en RendezVousDTO
        return list.stream()
                .map(rendezVous -> mapToDTO(rendezVous)) // Modification ici
                .collect(Collectors.toList());
    }
    public boolean supprimerRendezVous(Long rendezVousId) {
        Optional<RendezVous> rendezVousOptional = rendezVousRepo.findById(rendezVousId);
        if (rendezVousOptional.isPresent()) {
            rendezVousRepo.delete(rendezVousOptional.get());
            return true;
        } else {
            return false;
        }
    }

    public boolean updateRendezVous(RendezVous updatedRendezVous) {
        Optional<RendezVous> existingRendezVousOptional = rendezVousRepo.findById(updatedRendezVous.getAppointmentId());
        if (existingRendezVousOptional.isPresent()) {
            RendezVous existingRendezVous = existingRendezVousOptional.get();

            // Maintenir l'ID du professionnel (et d'autres champs que vous ne voulez pas modifier)
            updatedRendezVous.setProfessional(existingRendezVous.getProfessional());

            // Mettre à jour seulement les champs spécifiques
            existingRendezVous.setDate(updatedRendezVous.getDate());
            existingRendezVous.setDebut(updatedRendezVous.getDebut());
            existingRendezVous.setFin(updatedRendezVous.getFin());
            existingRendezVous.setStatuts(updatedRendezVous.getStatuts());

            // Enregistrez l'objet mis à jour
            rendezVousRepo.save(existingRendezVous);
            return true;
        } else {
            return false;
        }
    }

    public Optional<RendezVous> findById(Long id) {
        return rendezVousRepo.findById(id);
    }


    @Transactional
    public void addRendezVous(Long professionnelId, RendezVous rendezVous) {
        // Find the professional by ID
        Professionnel professionnel = professionnelRepo.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel non trouvé avec l'ID : " + professionnelId));

        // Associate the professional with the rendez-vous
        rendezVous.setProfessional(professionnel);

        // Save the rendez-vous to the database
        rendezVousRepo.save(rendezVous);
    }


}
