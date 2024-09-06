package com.example.pfe.Repo;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import com.example.pfe.Domain.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProfessionnelRepo extends JpaRepository<Professionnel, Long> {
    @Transactional
    void deleteByUserUserId(Long userId);
    @Query("SELECT p FROM Professionnel p JOIN p.rendezVous r WHERE r = :rendezVous")
    Professionnel findByRendezVous(@Param("rendezVous") RendezVous rendezVous);
}
