package com.example.pfe.Domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paiementId;

    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private Statut statutPaiement;

    private String methodePaiement;

    private LocalDateTime datePaiement;

    private String devise;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id", unique = true)
    private RendezVous rendezVous;


}