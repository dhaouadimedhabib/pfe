package com.example.pfe.Service;

import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.ServiceRepo;
import com.example.pfe.exception.DuplicateServiceException;
import com.example.pfe.exception.ResourceNotFoundException;

import com.example.pfe.Domain.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ServiceService {
    @Autowired
    private ServiceRepo serviceRepository;

    @Autowired
    private ProfessionnelRepo professionnelRepository;

    public Services createService(Services service, Long professionnelId) {
        // Vérifier si le professionnel existe
        Professionnel professionnel = professionnelRepository.findById(professionnelId)
                .orElse(null);

        if (professionnel == null) {
            // Professionnel non trouvé, retourne null pour gérer l'erreur dans le contrôleur
            return null;
        }

        // Vérifier si un service existe déjà pour ce professionnel
        if (serviceRepository.existsByProfessionnel(professionnel)) {
            // Service déjà existant, retourne null pour gérer l'erreur dans le contrôleur
            return null;
        }

        // Associer le professionnel au service
        service.setProfessionnel(professionnel);
        // Sauvegarder le service
        return serviceRepository.save(service);
    }

    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    public Services getServiceByProfessionnelId(Long professionnelId) {
        Services service = serviceRepository.findByProfessionnel_IdProfessionnel(professionnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found for this professionnel id :: " + professionnelId));
        // Accéder explicitement à l'association pour forcer l'initialisation
        service.getProfessionnel().getIdProfessionnel();
        return service;
    }

    public Services updateService(Long professionnelId, Services serviceDetails) {
        // Trouver le service associé au professionnel spécifié
        Services existingService = serviceRepository.findByProfessionnel_IdProfessionnel(professionnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found for this professionnelId :: " + professionnelId));

        // Mettre à jour les détails du service
        existingService.setNom(serviceDetails.getNom());
        existingService.setDescription(serviceDetails.getDescription());
        existingService.setDuree(serviceDetails.getDuree());
        existingService.setPrix(serviceDetails.getPrix());

        // Sauvegarder les modifications
        return serviceRepository.save(existingService);
    }
    public void deleteService(Long id) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found for this id :: " + id));
        serviceRepository.delete(service);
    }
}
