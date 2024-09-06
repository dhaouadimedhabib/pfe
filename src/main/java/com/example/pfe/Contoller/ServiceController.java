package com.example.pfe.Contoller;

import com.example.pfe.Domain.Services;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Service.ServiceService;
import com.example.pfe.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ProfessionnelRepo professionnelRepository;
    @PostMapping("/{professionnelId}")
    public ResponseEntity<?> createService(@RequestBody Services service, @PathVariable Long professionnelId) {
        // Appeler la méthode createService du ServiceService
        Services createdService = serviceService.createService(service, professionnelId);

        if (createdService == null) {
            // Vérifier si le professionnel existe
            if (!professionnelRepository.existsById(professionnelId)) {
                // Professionnel non trouvé
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Professionnel not found for this id :: " + professionnelId);
            }

            // Service déjà existant pour ce professionnel
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A service already exists for this professionnel. Only one service is allowed per professionnel.");
        }

        // Renvoie une réponse 200 OK avec le service créé
        return ResponseEntity.ok(createdService);
    }



    @GetMapping
    public List<Services> getAllServices() {
        return serviceService.getAllServices();
    }

    @GetMapping("/{professionnelId}")
    public ResponseEntity<Services> getServiceByProfessionnelId(@PathVariable Long professionnelId) {
        Services service = serviceService.getServiceByProfessionnelId(professionnelId);
        return ResponseEntity.ok(service);
    }


    @PutMapping("/{professionnelId}")
    public ResponseEntity<?> updateService(@PathVariable Long professionnelId, @RequestBody Services serviceDetails) {
        try {
            Services updatedService = serviceService.updateService(professionnelId, serviceDetails);
            return ResponseEntity.ok(updatedService);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
