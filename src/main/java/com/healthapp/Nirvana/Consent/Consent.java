package com.healthapp.Nirvana.Consent;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private Long doctorId; // resolved from email at invite time
    @Enumerated(EnumType.STRING)
    private ConsentStatus status; // GRANTED, REVOKED
    private LocalDateTime grantedAt;
    private LocalDateTime revokedAt;
}
