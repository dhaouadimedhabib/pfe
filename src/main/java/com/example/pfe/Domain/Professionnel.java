package com.example.pfe.Domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id", referencedColumnName = "serviceId")
    @JsonIgnore
    private Services service;

    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous;
    @OneToMany(mappedBy = "professionnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilite> disponibilites;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
