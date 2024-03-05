package com.example.pfe.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private LocalDateTime dateTime;
    @Enumerated(EnumType.STRING)
    private Statut statuts;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professionnel professional;
    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL)
    private Paiement paiement;
    @ManyToOne
    @JoinColumn(name = "commentaire_id")
    private Commentaire commentaire;
}
