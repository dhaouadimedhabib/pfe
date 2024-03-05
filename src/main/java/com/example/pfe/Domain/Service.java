package com.example.pfe.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@NoArgsConstructor
@Entity

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter

@AllArgsConstructor
public class Service {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    private String nom;
    private String description;
    private Duration duree;
    private BigDecimal prix;
    @ManyToMany(mappedBy = "servicesProposés", cascade = CascadeType.ALL)
    private List<Professionnel> professionnels;
}
