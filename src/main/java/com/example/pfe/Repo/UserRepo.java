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
    @Query(value = "SELECT * FROM user WHERE username = ?1 AND enabled = 1", nativeQuery = true)
    Integer findUserByEnabled(String username);

    //update password by email
    @Query(value = "UPDATE user SET password = ?1 WHERE email = ?2", nativeQuery = true)
    Integer updatePasswordByEmail(String password, String email);


    //Query requette get by typeFormation and status
    @Query(value = "SELECT * FROM user WHERE type_formation = ?1 AND enabled = ?2", nativeQuery = true)
    List<User> findByTypeFormationAndStatus(String typeFormation, Boolean status);

    //Query requette get by role
    @Query(value = "SELECT * FROM user WHERE role = ?1", nativeQuery = true)
    List<User> findByRoles(String role);

    //Query requette get by email
    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    User findByEmail(String email);

    //query update enabled
    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET enabled = ?1 WHERE id = ?2", nativeQuery = true)
    Integer updateEnabled(Long enabled, Long id);
    @Transactional
    void deleteByUserId(Long userId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") RoleName roleName);

    User findByUserId(Long userId);

}
