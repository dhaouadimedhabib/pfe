package com.example.pfe.Repo;

import com.example.pfe.Domain.Role;
import com.example.pfe.Domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);

}
