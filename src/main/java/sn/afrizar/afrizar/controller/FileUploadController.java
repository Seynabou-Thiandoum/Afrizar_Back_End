package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "API de gestion des fichiers")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    @PostMapping("/upload")
    @Operation(summary = "Upload un fichier", description = "Upload une image (max 5MB) - Accessible publiquement")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validation du fichier
            if (file.isEmpty()) {
                response.put("error", "Le fichier est vide");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérifier la taille
            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("error", "Le fichier est trop volumineux (max 5MB)");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérifier l'extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isValidImageFile(originalFilename)) {
                response.put("error", "Format de fichier non autorisé. Utilisez: jpg, jpeg, png, gif, webp");
                return ResponseEntity.badRequest().body(response);
            }

            // Créer le dossier d'upload s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFilename);

            // Copier le fichier
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Fichier uploadé avec succès: {}", newFilename);

            // Retourner l'URL du fichier
            String fileUrl = "/api/files/" + newFilename;
            response.put("url", fileUrl);
            response.put("filename", newFilename);
            response.put("originalFilename", originalFilename);
            response.put("fullUrl", "http://localhost:8080" + fileUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Erreur lors de l'upload du fichier: {}", e.getMessage());
            response.put("error", "Erreur lors de l'upload du fichier");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{filename:.+}")
    @Operation(summary = "Récupérer un fichier", description = "Télécharge un fichier uploadé")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Déterminer le type de contenu
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                log.warn("Fichier non trouvé: {}", filename);
                return ResponseEntity.notFound().build();
            }

        } catch (MalformedURLException e) {
            log.error("Erreur lors de la récupération du fichier: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du type de fichier: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un fichier", description = "Supprime un fichier uploadé")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String filename) {
        Map<String, String> response = new HashMap<>();

        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Files.deleteIfExists(filePath);

            log.info("Fichier supprimé: {}", filename);
            response.put("message", "Fichier supprimé avec succès");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier: {}", e.getMessage());
            response.put("error", "Erreur lors de la suppression du fichier");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
}

