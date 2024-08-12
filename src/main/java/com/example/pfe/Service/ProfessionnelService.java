package com.example.pfe.Service;

import com.example.pfe.Domain.DayOfWeek;
import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.User;
import com.example.pfe.Repo.DisponibiliteRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProfessionnelService {
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @Autowired
    DisponibiliteRepo disponibiliteRepo;

    @Autowired
    UserRepo userRepo;
    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    private final JwtUtils jwtUtils;

    @Autowired
    public ProfessionnelService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public void definirDisponibilite(String token, Disponibilite disponibilite) {
        // Récupérer le nom d'utilisateur à partir du jeton JWT
        String username = getUserNameFromJwtToken(token);
          System.out.println(username+"******************************");
        // Rechercher l'utilisateur correspondant dans la base de données par son nom d'utilisateur
        User user = userRepo.findByUsername(username);
        if (user != null) {
            // Utiliser l'ID de l'utilisateur pour définir l'ID du professionnel
            Long professionnelId = user.getProfessionnel().getIdProfessionnel(); // Supposant que l'ID de l'utilisateur correspond à l'ID du professionnel

            // Continuer avec le reste de votre logique pour définir la disponibilité du professionnel
            Professionnel professionnel = professionnelRepo.findById(professionnelId)
                    .orElseThrow(() -> new RuntimeException("Professionnel non trouvé avec l'id " + professionnelId));

            disponibilite.setProfessionnel(professionnel);
            disponibiliteRepo.save(disponibilite);


        } else {
            throw new RuntimeException("Utilisateur non trouvé avec le nom d'utilisateur " + username);
        }

    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean planifierAgendaAnnuel(Long professionnelId, Map<LocalDate, Map<DayOfWeek, List<Disponibilite>>> agendaAnnuel) {
        // Récupérer le professionnel directement par son ID
        Professionnel professionnel = professionnelRepo.findById(professionnelId).orElse(null);
        if (professionnel == null) {
            System.out.println("Erreur : Professionnel non trouvé.");
            return false;
        }

        if (agendaAnnuel == null || agendaAnnuel.isEmpty()) {
            System.out.println("Erreur : Agenda annuel vide.");
            return false;
        }

        // Parcourir chaque date et sa disponibilité associée dans l'agenda annuel
        for (Map.Entry<LocalDate, Map<DayOfWeek, List<Disponibilite>>> dateEntry : agendaAnnuel.entrySet()) {
            LocalDate date = dateEntry.getKey();
            Map<DayOfWeek, List<Disponibilite>> disponibilitesParJour = dateEntry.getValue();

            for (Map.Entry<DayOfWeek, List<Disponibilite>> jourEntry : disponibilitesParJour.entrySet()) {
                DayOfWeek dayOfWeek = jourEntry.getKey();
                List<Disponibilite> disponibilites = jourEntry.getValue();

                // Pour chaque disponibilité, associer le professionnel, la date et le jour
                for (Disponibilite disponibilite : disponibilites) {
                    disponibilite.setProfessionnel(professionnel);
                    disponibilite.setDate(date);
                    disponibilite.setJour(dayOfWeek);
                }

                // Sauvegarder les nouvelles disponibilités dans la base de données
                disponibiliteRepo.saveAll(disponibilites);
            }
        }

        // Sauvegarder les modifications dans la base de données
        professionnelRepo.save(professionnel);

        return true;
    }

}
