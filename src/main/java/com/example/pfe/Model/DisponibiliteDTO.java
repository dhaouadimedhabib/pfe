package com.example.pfe.Model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.pfe.Domain.DayOfWeek;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisponibiliteDTO {
    private Long idDisponibilite;
    private DayOfWeek jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private LocalDate date;
    private Long professionnelId;



}
