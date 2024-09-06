package com.example.pfe.Repo;


import com.example.pfe.Domain.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface CommentaireRepo extends JpaRepository<Commentaire, Long> {
}
