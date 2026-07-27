package com.healthapp.Nirvana.Consent.Dto;

import lombok.Data;

import java.util.List;

@Data
public class PatientSummary {
    private Long ConsentId;
    private Long patientId;
    private String patientName;
    private String patientEmail;

    public PatientSummary(Long id, Long patientId, String username, String email, Long consent) {
        this.ConsentId = consent;
        this.patientId = patientId;
        this.patientName = username;
        this.patientEmail = email;
    }
}
