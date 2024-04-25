package com.example.pfe.Domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentaireId;

    private String commentaire;

    private int note;

    private LocalDateTime dateFeedback;

    @OneToMany(mappedBy = "commentaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RendezVous> rendezVousList;


}


