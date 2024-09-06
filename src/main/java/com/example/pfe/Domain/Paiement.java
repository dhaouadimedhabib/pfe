package com.example.pfe.Domain;

import javax.persistence.*;
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
    private String methodePaiement;
    private LocalDateTime datePaiement;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private String cardNumber;
    @OneToOne
    @JoinColumn(name = "rendez_vous_id", unique = true)
    private RendezVous rendezVous;


}