package com.example.pfe.Repo;


import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface RendezVousRepo extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findAllByProfessional(Professionnel professionnel);
    List<RendezVous> findByDateAndProfessional(LocalDate date, Professionnel professional);

}
