package com.example.pfe.Repo;


import com.example.pfe.Domain.Disponibilite;
import com.example.pfe.Domain.Professionnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisponibiliteRepo extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findAllByProfessionnel(Professionnel professionnel);;

    @Modifying
    @Query("DELETE FROM Disponibilite d WHERE YEAR(d.date) = :year")
    void deleteByYear(@Param("year") int year);
    @Transactional
    @Modifying
    @Query("delete from Disponibilite d where year(d.date) = :annee and d.professionnel.idProfessionnel = :professionnelId")
    void deleteByYearAndProfessionnel(int annee, Long professionnelId);




}
