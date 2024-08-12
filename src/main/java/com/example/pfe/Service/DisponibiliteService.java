package com.example.pfe.Service;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.RendezVous;
import com.example.pfe.Domain.User;
import com.example.pfe.Model.DisponibiliteDTO;
import com.example.pfe.Repo.DisponibiliteRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.RendezVousRepo;
import com.example.pfe.Repo.UserRepo;
import com.example.pfe.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DisponibiliteService {
    @Autowired
    DisponibiliteRepo disponibiliteRepo;

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @Autowired
    private RendezVousRepo rendezVousRepo;
    private DisponibiliteDTO mapToDTO(final Disponibilite disponibilite, final DisponibiliteDTO disponibiliteDTO) {
        disponibiliteDTO.setIdDisponibilite(disponibilite.getIdDisponibilite());

        disponibiliteDTO.setHeureDebut1(disponibilite.getHeureDebut1());
        disponibiliteDTO.setHeureFin1(disponibilite.getHeureFin1());
        disponibiliteDTO.setHeureDebut2(disponibilite.getHeuredebut2());
        disponibiliteDTO.setHeureFin2(disponibilite.getHeureFin2());
        disponibiliteDTO.setDate(disponibilite.getDate());
        // Supposant que tu veux également mapper l'ID du professionnel associé à cette disponibilité
        disponibiliteDTO.setProfessionnelId(disponibilite.getProfessionnel() != null ? disponibilite.getProfessionnel().getIdProfessionnel() : null);

        return disponibiliteDTO;
    }

    public List<DisponibiliteDTO> findAllDisponibilites() {
        final List<Disponibilite> disponibilites = disponibiliteRepo.findAll();
        return disponibilites.stream()
                .map(disponibilite -> mapToDTO(disponibilite, new DisponibiliteDTO()))
                .collect(Collectors.toList());
    }

    public List<DisponibiliteDTO> findAllByProfessionnelDTO(Long idProfessionnel) {
        // Rechercher le professionnel par son identifiant
        Professionnel professionnel = professionnelRepo.findById(idProfessionnel).orElse(null);
        if (professionnel == null) {
            System.out.println("Erreur : Professionnel non trouvé.");
            return Collections.emptyList();
        }

        // Récupérer les disponibilités associées à ce professionnel
        List<Disponibilite> disponibilites = disponibiliteRepo.findAllByProfessionnel(professionnel);

        // Mapper les disponibilités en DTO
        return disponibilites.stream()
                .map(disponibilite -> mapToDTO(disponibilite, new DisponibiliteDTO()))
                .collect(Collectors.toList());
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public DisponibiliteDTO addDisponibilite(Long professionnelId, DisponibiliteDTO disponibiliteDTO) {
        Disponibilite disponibilite = new Disponibilite();
        mapToEntity(disponibiliteDTO, disponibilite);

        Professionnel professionnel = professionnelRepo.findById(professionnelId)
                .orElseThrow(() -> new RuntimeException("Professionnel not found"));
        disponibilite.setProfessionnel(professionnel);
        if (disponibilite.getDate() == null) {
            disponibilite.setDate(LocalDate.now()); // Définir la date sur la date actuelle si elle est null
        }

        Disponibilite savedDisponibilite = disponibiliteRepo.save(disponibilite);
        return mapToDTO(savedDisponibilite, new DisponibiliteDTO());
    }
    public DisponibiliteDTO editDisponibilite(Long disponibiliteId, DisponibiliteDTO disponibiliteDTO) {
        Disponibilite disponibilite = disponibiliteRepo.findById(disponibiliteId)
                .orElseThrow(() -> new RuntimeException("Disponibilite not found"));

        // Mise à jour des champs
        disponibilite.setHeureDebut1(disponibiliteDTO.getHeureDebut1());
        disponibilite.setHeureFin1(disponibiliteDTO.getHeureFin1());
        disponibilite.setHeuredebut2(disponibiliteDTO.getHeureDebut2());
        disponibilite.setHeureFin2(disponibiliteDTO.getHeureFin2());
        disponibilite.setDate(disponibiliteDTO.getDate());

        Disponibilite updatedDisponibilite = disponibiliteRepo.save(disponibilite);
        return mapToDTO(updatedDisponibilite, new DisponibiliteDTO());
    }


    public void deleteDisponibilite(Long disponibiliteId) {
        Disponibilite disponibilite = disponibiliteRepo.findById(disponibiliteId)
                .orElseThrow(() -> new RuntimeException("Disponibilite not found"));
        disponibiliteRepo.delete(disponibilite);
    }


    public List<Disponibilite> getDisponibilitesByDateAndProfessionnel(LocalDate date, Long professionnelId) {
        Professionnel professionnel = professionnelRepo.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel not found with id: " + professionnelId));
        return disponibiliteRepo.findByDateAndProfessionnel(date, professionnel);
    }

    public List<String> getTimeSlots(LocalTime start, LocalTime end) {
        List<String> timeSlots = new ArrayList<>();
        while (start.isBefore(end)) {
            LocalTime next = start.plusMinutes(30);
            if (next.isAfter(end)) {
                break;
            }
            timeSlots.add(start + "-" + next);
            start = next;
        }
        return timeSlots;
    }

    public List<String> getAllTimeSlots(Disponibilite disponibilite, Long professionnelId) {
        // Fetch appointments for the professional on the given date
        List<RendezVous> rendezVousList = rendezVousRepo.findByDateAndProfessional(disponibilite.getDate(), disponibilite.getProfessionnel());
        List<String> timeSlots = new ArrayList<>();
        List<String> slots1 = getTimeSlots(disponibilite.getHeureDebut1(), disponibilite.getHeureFin1());
        List<String> slots2 = getTimeSlots(disponibilite.getHeuredebut2(), disponibilite.getHeureFin2());

        // Exclude overlapping time slots
        for (String slot : slots1) {
            if (!isSlotOverlapping(slot, rendezVousList)) {
                timeSlots.add(slot);
            }
        }
        for (String slot : slots2) {
            if (!isSlotOverlapping(slot, rendezVousList)) {
                timeSlots.add(slot);
            }
        }

        return timeSlots;
    }

    private boolean isSlotOverlapping(String slot, List<RendezVous> rendezVousList) {
        String[] times = slot.split("-");
        LocalTime slotStart = LocalTime.parse(times[0]);
        LocalTime slotEnd = LocalTime.parse(times[1]);

        for (RendezVous rdv : rendezVousList) {
            if ((slotStart.isBefore(rdv.getFin()) && slotEnd.isAfter(rdv.getDebut())) ||
                    (slotStart.equals(rdv.getDebut()) || slotEnd.equals(rdv.getFin()))) {
                return true;
            }
        }
        return false;
    }
    private Disponibilite mapToEntity(final DisponibiliteDTO disponibiliteDTO, final Disponibilite disponibilite) {
        disponibilite.setHeureDebut1(disponibiliteDTO.getHeureDebut1());
        disponibilite.setHeureFin1(disponibiliteDTO.getHeureFin1());
        disponibilite.setHeuredebut2(disponibiliteDTO.getHeureDebut2());
        disponibilite.setHeureFin2(disponibiliteDTO.getHeureFin2());
        disponibilite.setDate(disponibiliteDTO.getDate());
        if (disponibiliteDTO.getProfessionnelId() != null) {
            Professionnel professionnel = professionnelRepo.findById(disponibiliteDTO.getProfessionnelId())
                    .orElseThrow(() -> new RuntimeException("Professionnel not found"));
            disponibilite.setProfessionnel(professionnel);
        }
        return disponibilite;
    }
}
