package com.example.pfe.Contoller;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Model.DisponibiliteDTO;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Service.DisponibiliteService;
import com.example.pfe.Service.ProfessionnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/disponibilitespro")
    public ResponseEntity<List<DisponibiliteDTO>> getAllDisponibilitesByProfessionnelDTO(@RequestHeader("Authorization") String token) {
        List<DisponibiliteDTO> disponibiliteDTOs = disponibiliteService.findAllByProfessionnelDTO(token);
        if (disponibiliteDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(disponibiliteDTOs, HttpStatus.OK);
    }
}
