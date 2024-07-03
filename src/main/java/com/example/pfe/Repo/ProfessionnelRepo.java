package com.example.pfe.Repo;

import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProfessionnelRepo extends JpaRepository<Professionnel, Long> {
    @Transactional
    void deleteByUserUserId(Long userId);
}
