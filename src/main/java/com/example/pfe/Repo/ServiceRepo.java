package com.example.pfe.Repo;


import com.example.pfe.Domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ServiceRepo extends JpaRepository<Service, Long> {
}
