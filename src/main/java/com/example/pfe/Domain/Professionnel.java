package com.example.pfe.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professionnel extends User {



    @ManyToMany
    @JoinTable(
            name = "professionnel_service",
            joinColumns = @JoinColumn(name = "professionnel_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> servicesProposés;

    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HeureDeTravail> heuresDeTravail;

    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous;

    @OneToOne(mappedBy = "professionnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DetailsProfessionnel detailsProfessionnel;
}
