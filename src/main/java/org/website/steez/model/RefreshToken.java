package org.website.steez.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@Data
public class RefreshToken {
    
    @Id
    private String username;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private Instant expiryDate;
}
