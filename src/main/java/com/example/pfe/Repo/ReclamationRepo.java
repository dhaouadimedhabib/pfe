package com.example.pfe.Repo;

import com.example.pfe.Domain.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ReclamationRepo extends JpaRepository<Reclamation, Long> {
}
