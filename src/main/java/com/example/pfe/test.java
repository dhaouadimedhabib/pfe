package com.example.pfe;

import jakarta.persistence.*;

@Entity
public class test {
    @jakarta.persistence.Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
}
