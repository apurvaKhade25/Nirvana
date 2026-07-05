package com.healthapp.Nirvana.Consent;

import com.healthapp.Nirvana.Consent.Dto.Consent;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;


@Component
public class ConsentGuard {
    private final ConsentRepo consentRepo;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConsentGuard.class);

    public ConsentGuard(ConsentRepo consentRepo) {
        this.consentRepo = consentRepo;
    }

    public void verify(Long doctorId, Long patientId) {
        Consent consent = consentRepo
                .findBydoctorIdAndPatientId(doctorId, patientId)
                .orElseThrow(() -> {
                    log.warn("No consent record found for doctor: {} and patient: {}", doctorId, patientId);
                    return new AccessDeniedException("No consent record");
                });

        if (consent.getStatus() != ConsentStatus.GRANTED) {
            log.warn("Consent not granted for doctor: {} and patient: {}", doctorId, patientId);
            throw new AccessDeniedException("Consent not granted");
        }
    }
}
