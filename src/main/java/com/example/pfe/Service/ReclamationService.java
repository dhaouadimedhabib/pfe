package com.example.pfe.Service;

import com.example.pfe.Domain.Notification;
import com.example.pfe.Domain.Reclamation;
import com.example.pfe.Repo.NotificationRepo;
import com.example.pfe.Repo.ReclamationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationService {
    @Autowired
    ReclamationRepo reclamationRepo;
    @Autowired
    NotificationRepo notificationRepo;
    public Reclamation saveReclamation(Reclamation reclamation) {
        Notification notification = new Notification(
                "Nouvelle Demande de Swap",
                OffsetDateTime.now().toInstant()
        );
        notificationRepo.save(notification);
        return reclamationRepo.save(reclamation);
    }


    public void deleteReclamation(Long id) {
        if (reclamationRepo.existsById(id)) {
            reclamationRepo.deleteById(id);
        } else {
            throw new IllegalArgumentException("Reclamation not found with id: " + id);
        }
    }

    public Reclamation getReclamationById(Long id) {
        Optional<Reclamation> reclamation = reclamationRepo.findById(id);
        if (reclamation.isPresent()) {
            return reclamation.get();
        } else {
            throw new IllegalArgumentException("Reclamation not found with id: " + id);
        }
    }


    public List<Reclamation> getAllReclamations() {
        return reclamationRepo.findAll();
    }

    public Reclamation updateReclamation(Long id, Reclamation updatedReclamation) {
        Optional<Reclamation> existingReclamation = reclamationRepo.findById(id);
        if (existingReclamation.isPresent()) {
            Reclamation reclamation = existingReclamation.get();
            reclamation.setName(updatedReclamation.getName());
            reclamation.setEmail(updatedReclamation.getEmail());
            reclamation.setPhone(updatedReclamation.getPhone());
            reclamation.setMessage(updatedReclamation.getMessage());
            return reclamationRepo.save(reclamation);
        } else {
            throw new IllegalArgumentException("Reclamation not found with id: " + id);
        }
    }
}
