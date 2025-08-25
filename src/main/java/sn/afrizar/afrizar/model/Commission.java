package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commissions")
public class Commission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Seuil minimum pour cette tranche (en FCFA)
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal seuilMin;
    
    // Seuil maximum pour cette tranche (null = pas de limite)
    @Column(precision = 10, scale = 2)
    private BigDecimal seuilMax;
    
    // Pourcentage de commission (ex: 10.00 pour 10%)
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal pourcentage;
    
    // Description de la tranche
    private String description;
    
    // Active ou non
    private boolean active = true;
    
    // Ordre d'application (important pour les tranches)
    private Integer ordre = 0;
    
    /**
     * Vérifie si un montant correspond à cette tranche de commission
     */
    public boolean correspondAuMontant(BigDecimal montant) {
        if (montant.compareTo(seuilMin) < 0) {
            return false;
        }
        return seuilMax == null || montant.compareTo(seuilMax) <= 0;
    }
    
    /**
     * Calcule le montant de commission pour un prix donné
     */
    public BigDecimal calculerCommission(BigDecimal prix) {
        if (!correspondAuMontant(prix)) {
            return BigDecimal.ZERO;
        }
        return prix.multiply(pourcentage).divide(BigDecimal.valueOf(100));
    }
}

