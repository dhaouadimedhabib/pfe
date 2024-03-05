package com.example.pfe.Domain;

import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
public class User {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long userId ;
    @Column
   private String userName;
    @Column
   private  String  password;
    @Column
   private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private Set<Role> roles;


}
