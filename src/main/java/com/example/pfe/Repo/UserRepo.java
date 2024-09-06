package com.example.pfe.Repo;

import com.example.pfe.Domain.RoleName;
import com.example.pfe.Domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    //Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    User findByToken(String token);







    //Query requette get by email
    @Query("SELECT u FROM User u WHERE u.email = :email")
    List<User> findByEmail(@Param("email") String email);


    @Transactional
    void deleteByUserId(Long userId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") RoleName roleName);




    @Query("SELECT u FROM User u "
            + "JOIN u.professionnel p "
            + "JOIN p.service s "
            + "WHERE s.nom LIKE %:serviceName%")
    List<User> findProfessionalsByServiceName(@Param("serviceName") String serviceName);
}
