package com.example.pfe.Domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Duration;

@NoArgsConstructor
@Entity

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Services {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    private String nom;
    private String description;
    private Duration duree;
    private BigDecimal prix;
    @OneToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "professionnel_id", referencedColumnName = "idProfessionnel")
    @JsonIgnore
    private Professionnel professionnel;
}
