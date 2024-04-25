package com.example.pfe.Domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Entity

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
public class RendezVous {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;
    private LocalDate date;
    private LocalTime debut; // Date et heure de début du rendez-vous
    private LocalTime fin;
    @Enumerated(EnumType.STRING)
    private Statut statuts;


    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professionnel professional;
    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL)
    private Paiement paiement;
    @ManyToOne
    @JoinColumn(name = "commentaire_id")
    private Commentaire commentaire;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
