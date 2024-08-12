package com.example.pfe.Contoller;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Model.DisponibiliteDTO;
import com.example.pfe.Model.RendezVousDTO;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Service.DisponibiliteService;
import com.example.pfe.Service.ProfessionnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/Disponibilite")
public class DisponibiliteController {
    @Autowired
    DisponibiliteService disponibiliteService;
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @GetMapping("/disponibilites")
    public List<DisponibiliteDTO> getAllDisponibilites() {
        return disponibiliteService.findAllDisponibilites();
    }

    @GetMapping("/disponibilitespro/{idProfessionnel}")
    public ResponseEntity<List<DisponibiliteDTO>> getAllDisponibilitesByProfessionnelDTO(@PathVariable  Long idProfessionnel) {
        List<DisponibiliteDTO> disponibiliteDTOs = disponibiliteService.findAllByProfessionnelDTO(idProfessionnel);
        if (disponibiliteDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(disponibiliteDTOs, HttpStatus.OK);
    }
    @PostMapping("/{professionnelId}")
    public ResponseEntity<?> addDisponibilite(@PathVariable Long professionnelId, @RequestBody DisponibiliteDTO disponibiliteDTO) {
        try {
            DisponibiliteDTO createdDisponibiliteDTO = disponibiliteService.addDisponibilite(professionnelId, disponibiliteDTO);
            return ResponseEntity.ok(createdDisponibiliteDTO);
        } catch (IllegalArgumentException ex) {
            // Si l'exception est levée (par exemple, validation des données)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            // Autres exceptions non prévues
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de l'ajout de la disponibilité.");
        }
    }

    @PutMapping("/{disponibiliteId}")
    public ResponseEntity<?> editDisponibilite(@PathVariable Long disponibiliteId, @RequestBody DisponibiliteDTO disponibiliteDTO) {
        try {
            DisponibiliteDTO updatedDisponibiliteDTO = disponibiliteService.editDisponibilite(disponibiliteId, disponibiliteDTO);
            return ResponseEntity.ok(updatedDisponibiliteDTO);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la modification de la disponibilité.");
        }
    }

    @DeleteMapping("/{disponibiliteId}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long disponibiliteId) {
        disponibiliteService.deleteDisponibilite(disponibiliteId);
        return ResponseEntity.noContent().build();
    }

        @GetMapping("/by-date/{professionnelId}/{date}")
        public ResponseEntity<List<String>> getDisponibilitesByDateAndProfessionnel(
                @PathVariable("professionnelId") Long professionnelId,
                @PathVariable("date") String date) {
            LocalDate localDate = LocalDate.parse(date);
            List<Disponibilite> disponibilites = disponibiliteService.getDisponibilitesByDateAndProfessionnel(localDate, professionnelId);

            List<String> allTimeSlots = new ArrayList<>();
            for (Disponibilite disponibilite : disponibilites) {
                allTimeSlots.addAll(disponibiliteService.getAllTimeSlots(disponibilite, professionnelId));
            }

            if (allTimeSlots.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(allTimeSlots);
            }
        }
}
