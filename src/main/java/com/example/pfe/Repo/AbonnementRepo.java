package com.example.pfe.Repo;


import com.example.pfe.Domain.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AbonnementRepo extends JpaRepository<Abonnement, Long> {
}
