package com.example.pfe.Contoller;

import com.example.pfe.Domain.Client;
import com.example.pfe.Domain.RendezVous;
import com.example.pfe.Model.RendezVousDTO;
import com.example.pfe.Service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/RendezVous")
public class RendezVousController {
    @Autowired
    private RendezVousService rendezVousService;

    /*
    @GetMapping("/rendezvous")
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVousByProfessionnel(@RequestHeader("Authorization") String token) {
        List<RendezVousDTO> rendezVousDTOs = rendezVousService.findAllRendezVousByProfessionnel(token);
        if (rendezVousDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(rendezVousDTOs, HttpStatus.OK);
    }
*/

    @GetMapping("/professionnel")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByProfessionnel(
            @RequestHeader("Authorization") String authorizationHeader) {
        // Supposons que le token JWT soit dans le format "Bearer <token>"
        String token = authorizationHeader.replace("Bearer ", ""); // Extrait le token

        List<RendezVousDTO> rendezVousList = rendezVousService.findAllRendezVousByProfessionnel(token);

        if (rendezVousList.isEmpty()) {
            // Si aucun rendez-vous n'est trouvé, renvoyer HTTP 404
            return ResponseEntity.notFound().build();
        }

        // Sinon, renvoyer la liste des rendez-vous avec HTTP 200
        return ResponseEntity.ok(rendezVousList);
    }


    @PostMapping("/ajouter-rendezvous")
    public ResponseEntity<String> ajouterRendezVous(@RequestHeader("Authorization") String token, @RequestBody RendezVousDTO nouveauRendezVousDTO) {
        String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "

        boolean ajoutReussi = rendezVousService.ajouterRendezVous(jwtToken, nouveauRendezVousDTO);

        if (ajoutReussi) {
            return ResponseEntity.ok("Le rendez-vous a été ajouté avec succès !");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible d'ajouter le rendez-vous. Veuillez vérifier les disponibilités et réessayer.");
        }
    }
    @GetMapping
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVous(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        List<RendezVousDTO> rendezVous = rendezVousService.findAllRendezVous(token);
        if (rendezVous.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVous);
    }
}
