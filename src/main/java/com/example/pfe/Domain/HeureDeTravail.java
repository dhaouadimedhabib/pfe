package com.example.pfe.Domain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeureDeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long heureDeTravailId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek jourDeLaSemaine;

    private LocalTime heureDeDébut;
    private LocalTime heureDeFin;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professionnel professional;
}