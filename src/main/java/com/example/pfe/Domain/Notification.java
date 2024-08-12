package com.example.pfe.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Instant timestamp;

    public Notification(String title, Instant timestamp) {
        this.title = title;
        this.timestamp = timestamp;
    }

    public Notification() {

    }

    public Notification(Long id, String title, Instant timestamp) {
        this.id = id;
        this.title = title;
        this.timestamp = timestamp;
    }
}

