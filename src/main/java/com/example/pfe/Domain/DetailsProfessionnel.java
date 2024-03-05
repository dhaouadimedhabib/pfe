package com.example.pfe.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailsProfessionnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    private String specialisation;
    private String biographie;

    @OneToOne
    @JoinColumn(name = "professional_id")
    private Professionnel professionnel;
}
