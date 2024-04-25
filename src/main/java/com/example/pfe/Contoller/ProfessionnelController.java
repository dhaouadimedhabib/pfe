package com.example.pfe.Contoller;

import com.example.pfe.Domain.DayOfWeek;
import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Repo.DisponibiliteRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Service.ProfessionnelService;
import com.example.pfe.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/Professionnel")
public class ProfessionnelController {
    @Autowired
    private ProfessionnelService professionnelService;
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @Autowired
    DisponibiliteRepo disponibiliteRepo;
   @Autowired
   JwtUtils jwtUtils;


    @PostMapping("/definir-disponibilite")
    public ResponseEntity<?> definirDisponibilite(@RequestHeader("Authorization") String token,
                                                  @RequestBody Disponibilite disponibilite) {
        try {
            // Appel de la méthode pour définir la disponibilité en utilisant le token JWT
            professionnelService.definirDisponibilite(token, disponibilite);
            return ResponseEntity.ok().body("Disponibilité définie avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PostMapping("/exemple")
    public ResponseEntity<?> exempleMethode(@RequestHeader("Authorization") String token) {
        try {
            // Appel de la méthode pour récupérer le nom d'utilisateur à partir du token JWT
            String username = jwtUtils.getUserNameFromJwtToken(token);

            // Vous pouvez utiliser le nom d'utilisateur comme vous le souhaitez ici

            return ResponseEntity.ok().body("Nom d'utilisateur récupéré avec succès : " + username);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Erreur lors de la récupération du nom d'utilisateur : " + ex.getMessage());
        }
    }


    @PostMapping("/planifier-agenda-annuel")
    public ResponseEntity<String> planifierAgendaAnnuel(@RequestHeader("Authorization") String token, @RequestBody Map<LocalDate, Map<DayOfWeek, List<Disponibilite>>> agendaAnnuel) {
        boolean result = professionnelService.planifierAgendaAnnuel(token, agendaAnnuel);

        if (result) {
            return ResponseEntity.ok("Agenda annuel planifié avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Erreur lors de la planification de l'agenda annuel.");
        }
    }

}
