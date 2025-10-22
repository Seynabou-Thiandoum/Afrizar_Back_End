package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"client", "items"})
@Table(name = "paniers")
public class Panier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;
    
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PanierItem> items = new ArrayList<>();
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(nullable = false)
    private boolean actif = true;
    
    // Méthodes utilitaires
    
    public void ajouterItem(PanierItem item) {
        items.add(item);
        item.setPanier(this);
        this.dateModification = LocalDateTime.now();
    }
    
    public void retirerItem(PanierItem item) {
        items.remove(item);
        item.setPanier(null);
        this.dateModification = LocalDateTime.now();
    }
    
    public void vider() {
        items.clear();
        this.dateModification = LocalDateTime.now();
    }
    
    public BigDecimal getMontantTotal() {
        return items.stream()
                .map(PanierItem::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getNombreTotalArticles() {
        return items.stream()
                .mapToInt(PanierItem::getQuantite)
                .sum();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}



