package com.example.pfe.Domain;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private DayOfWeek jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
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

    public void setJour(DayOfWeek jour) {
        this.jour = jour;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Professionnel getProfessionnel() {
        return professionnel;
    }

    public void setProfessionnel(Professionnel professionnel) {
        this.professionnel = professionnel;
    }

    public Disponibilite(Long idDisponibilite, DayOfWeek jour, LocalTime heureDebut, LocalTime heureFin, LocalDate date, Professionnel professionnel) {
        this.idDisponibilite = idDisponibilite;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.date = date;
        this.professionnel = professionnel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professionnelId") // Assure-toi que le nom correspond à la colonne de clé étrangère
    private Professionnel professionnel;
}
