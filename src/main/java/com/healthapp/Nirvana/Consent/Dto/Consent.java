package com.healthapp.Nirvana.Consent.Dto;

import com.healthapp.Nirvana.Consent.ConsentStatus;
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
    @GeneratedValue
    private Long id;
    private Long patientId;
    private Long doctorId; // resolved from email at invite time
    @Enumerated(EnumType.STRING)
    private ConsentStatus status; // GRANTED, REVOKED
    private LocalDateTime grantedAt;
    private LocalDateTime revokedAt;
}
