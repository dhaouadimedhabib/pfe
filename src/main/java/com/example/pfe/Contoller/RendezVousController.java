package com.example.pfe.Contoller;

import com.example.pfe.Domain.Client;
import com.example.pfe.Domain.RendezVous;
import com.example.pfe.Model.RendezVousDTO;
import com.example.pfe.Service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            return ResponseEntity.noContent().build(); // Retourner un code HTTP 204 No Content
        }

        return ResponseEntity.ok(rendezVousDTOs); // Retourner la liste des rendez-vous avec le code HTTP 200 OK
    }
*/

    @GetMapping("/professionnel")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByProfessionnel(
            @RequestHeader("Authorization") String token) {
        // Assurez-vous que le token JWT est valide avant de l'utiliser
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Supprimer le préfixe "Bearer " du token
            List<RendezVousDTO> rendezVousList = rendezVousService.findAllRendezVousByProfessionnel(token);

            if (rendezVousList.isEmpty()) {
                // Si aucun rendez-vous n'est trouvé, renvoyer HTTP 404
                return ResponseEntity.notFound().build();
            }

            // Sinon, renvoyer la liste des rendez-vous avec HTTP 200
            return ResponseEntity.ok(rendezVousList);
        } else {
            // Gérer le cas où le token n'est pas correctement formaté
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<RendezVousDTO>());
        }
    }
/*
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

*/


    @PostMapping("/ajouter/{professionnelId}")
    public ResponseEntity<?> ajouterRendezVous(@PathVariable Long professionnelId, @RequestBody RendezVousDTO nouveauRendezVousDTO) {
        try {
            boolean ajoutReussi = rendezVousService.ajouterRendezVous(professionnelId, nouveauRendezVousDTO);
            if (ajoutReussi) {
                return ResponseEntity.ok("Le rendez-vous a été ajouté avec succès.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout du rendez-vous.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de l'ajout du rendez-vous : " + e.getMessage());
        }
    }



    @GetMapping("/all")
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVous() {
        List<RendezVousDTO> rendezVousList = rendezVousService.findAllRendezVous();

        if (rendezVousList.isEmpty()) {
            // Si la liste est vide, retournez un code 404
            return ResponseEntity.notFound().build();
        }

        // Sinon, retournez la liste des rendez-vous avec un code 200
        return ResponseEntity.ok(rendezVousList);
    }




    @GetMapping("/{professionnelId}")
    @PreAuthorize("hasAnyRole('PROFESSIONAL')")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByProfessionnelId(@PathVariable Long professionnelId) {
        List<RendezVousDTO> rendezVousDTOs = rendezVousService.findAllRendezVousByProfessionnelId(professionnelId);
        return ResponseEntity.ok(rendezVousDTOs);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> supprimerRendezVous(@PathVariable Long id) {
        boolean isDeleted = rendezVousService.supprimerRendezVous(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRendezVous(@RequestBody RendezVous updatedRendezVous) {
        Boolean isUpdated = rendezVousService.updateRendezVous(updatedRendezVous);
        if (isUpdated) {
            return ResponseEntity.ok("Le rendez-vous a été mis à jour avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le rendez-vous n'existe pas.");
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable Long id) {
        Optional<RendezVous> rendezVous = rendezVousService.findById(id);
        if (rendezVous.isPresent()) {
            return ResponseEntity.ok(rendezVous.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
