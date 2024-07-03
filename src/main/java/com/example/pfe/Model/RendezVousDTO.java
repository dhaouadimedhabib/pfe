package com.example.pfe.Model;


import com.example.pfe.Domain.Statut;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class RendezVousDTO {
    private Long appointmentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private LocalTime debut;
    private LocalTime fin;
    private Statut statuts;
    private Long professionalId;
    private Long paiementId;
    private Long commentaireId;

   private  String nomClient;
}
