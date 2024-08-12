package com.example.pfe.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter

public class Client  {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long clientId;

    @OneToOne
    private Abonnement abonement;

    public Client(Long clientId, List<RendezVous> rendezVous, Abonnement abonement, User user) {
        this.clientId = clientId;

        this.abonement = abonement;
        this.user = user;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }



    public Abonnement getAbonement() {
        return abonement;
    }

    public void setAbonement(Abonnement abonement) {
        this.abonement = abonement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


}
