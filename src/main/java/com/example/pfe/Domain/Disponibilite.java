package com.example.pfe.Domain;

import javax.persistence.*;
import javax.validation.constraints.Null;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor


public class Disponibilite {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisponibilite;
    @Enumerated(EnumType.STRING)
    @Null
    private DayOfWeek jour;
    private LocalTime heureDebut1;
    private LocalTime heureFin1;
    @Column
    private  LocalTime heureDebut2;
    private  LocalTime heureFin2;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    private LocalDate date;

    public Long getIdDisponibilite() {
        return idDisponibilite;
    }

    public void setIdDisponibilite(Long idDisponibilite) {
        this.idDisponibilite = idDisponibilite;
    }





    public DayOfWeek getJour() {
        return jour;
    }

    public LocalTime getHeureDebut1() {
        return heureDebut1;
    }

    public LocalTime getHeureFin1() {
        return heureFin1;
    }

    public LocalTime getHeuredebut2() {
        return heureDebut2;
    }

    public LocalTime getHeureFin2() {
        return heureFin2;
    }

    public LocalDate getDate() {
        return date;
    }

    public Professionnel getProfessionnel() {
        return professionnel;
    }

    public void setJour(DayOfWeek jour) {
        this.jour = jour;
    }

    public void setHeureDebut1(LocalTime heureDebut1) {
        this.heureDebut1 = heureDebut1;
    }

    public void setHeureFin1(LocalTime heureFin1) {
        this.heureFin1 = heureFin1;
    }

    public void setHeuredebut2(LocalTime heuredebut2) {
        this.heureDebut2 = heuredebut2;
    }

    public void setHeureFin2(LocalTime heureFin2) {
        this.heureFin2 = heureFin2;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setProfessionnel(Professionnel professionnel) {
        this.professionnel = professionnel;
    }

    public Disponibilite(LocalTime heureDebut1, LocalTime heureFin1, LocalTime heureDebut2, LocalTime heureFin2, LocalDate date, Professionnel professionnel) {
        this.heureDebut1 = heureDebut1;
        this.heureFin1 = heureFin1;
        this.heureDebut2 = heureDebut2;
        this.heureFin2 = heureFin2;
        this.date = date;
        this.professionnel = professionnel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professionnelId")
    @JsonIgnore
    private Professionnel professionnel;

}
