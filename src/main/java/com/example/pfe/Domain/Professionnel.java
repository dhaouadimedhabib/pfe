package com.example.pfe.Domain;

import javax.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Professionnel  {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfessionnel;

    public Long getIdProfessionnel() {
        return idProfessionnel;
    }

    public void setIdProfessionnel(Long idProfessionnel) {
        this.idProfessionnel = idProfessionnel;
    }

    public List<Service> getServicesProposés() {
        return servicesProposés;
    }

    public void setServicesProposés(List<Service> servicesProposés) {
        this.servicesProposés = servicesProposés;
    }

    public List<RendezVous> getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(List<RendezVous> rendezVous) {
        this.rendezVous = rendezVous;
    }

    public List<Disponibilite> getDisponibilites() {
        return disponibilites;
    }

    public void setDisponibilites(List<Disponibilite> disponibilites) {
        this.disponibilites = disponibilites;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Professionnel(Long idProfessionnel, List<Service> servicesProposés, List<RendezVous> rendezVous, List<Disponibilite> disponibilites, User user) {
        this.idProfessionnel = idProfessionnel;
        this.servicesProposés = servicesProposés;
        this.rendezVous = rendezVous;
        this.disponibilites = disponibilites;
        this.user = user;
    }

    @ManyToMany
    @JoinTable(
            name = "professionnel_service",
            joinColumns = @JoinColumn(name = "professionnel_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> servicesProposés;



    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous;
    @OneToMany(mappedBy = "professionnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilite> disponibilites;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
