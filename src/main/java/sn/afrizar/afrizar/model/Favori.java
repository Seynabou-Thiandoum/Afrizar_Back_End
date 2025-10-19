package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favoris", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"client_id", "produit_id"})
})
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Utilisateur client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        dateAjout = LocalDateTime.now();
    }
}

