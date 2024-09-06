package com.example.pfe.Repo;

import com.example.pfe.Domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository

public interface ClientRepo extends JpaRepository<Client, Long> {
    @Transactional
    void deleteByUserUserId(Long userId);
}
