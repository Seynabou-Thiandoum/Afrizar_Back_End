package sn.afrizar.afrizar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Utilisateur;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    Optional<Utilisateur> findByEmailAndActif(String email, boolean actif);
    
    boolean existsByEmail(String email);
    
    List<Utilisateur> findByRole(Utilisateur.Role role);
    
    Page<Utilisateur> findByRole(Utilisateur.Role role, Pageable pageable);
    
    long countByActif(boolean actif);
    
    List<Utilisateur> findByActif(boolean actif);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.nom LIKE %:nom% OR u.prenom LIKE %:nom%")
    List<Utilisateur> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role AND u.actif = true")
    Long countByRoleAndActif(@Param("role") Utilisateur.Role role);
}

