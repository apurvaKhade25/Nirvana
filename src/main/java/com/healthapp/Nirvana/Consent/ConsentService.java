package com.healthapp.Nirvana.Consent;

import com.healthapp.Nirvana.Auth.AuthenticatedUserProvider;
import com.healthapp.Nirvana.Consent.Dto.DoctorSummary;
import com.healthapp.Nirvana.Consent.Dto.PatientSummary;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserRepo userRepo;
    private final ConsentRepo consentRepo;
    public void inviteDoctor(String doctorEmail) {
        // Logic to invite the doctor to sign the consent form

        Long patientId = authenticatedUserProvider.getAuthenticatedUserId();

        User doctor = userRepo.findByEmail(doctorEmail).orElseThrow(() -> new RuntimeException("Doctor not found"));

        Optional<Consent> existingConsent = consentRepo.findBydoctorIdAndPatientId(doctor.getId(), patientId);


        if (existingConsent.isPresent()) {
            Consent consent = existingConsent.get();
            if (consent.getStatus() == ConsentStatus.GRANTED) {
                throw new RuntimeException("Consent already exists between this doctor and patient");
            }

            // was REVOKED - reactivate it
            consent.setStatus(ConsentStatus.GRANTED);
            consent.setGrantedAt(LocalDateTime.now());
            consent.setRevokedAt(null);
            consentRepo.save(consent);
            return;
        }

        Consent consent = new Consent();
        consent.setPatientId(patientId);
        consent.setDoctorId(doctor.getId());
        consent.setStatus(ConsentStatus.GRANTED);
        consent.setGrantedAt(LocalDateTime.now());
        consentRepo.save(consent);
    }

    public void revoke(Long consentId) {
        Long patientId = authenticatedUserProvider.getAuthenticatedUserId();

        Consent consent = consentRepo.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consent not found"));

        if (!consent.getPatientId().equals(patientId)) {
            throw new RuntimeException("Not your consent record to revoke");
        }

        consent.setStatus(ConsentStatus.REVOKED);
        consent.setRevokedAt(LocalDateTime.now());
        consentRepo.save(consent);
    }

    // Method to check if a doctor has consent from a patient
    public List<PatientSummary> getPatientSummaries() {
        Long authenticatedDoctorId = authenticatedUserProvider.getAuthenticatedUserId();
        List <Consent> consents = consentRepo.findByDoctorIdAndStatus(authenticatedDoctorId, ConsentStatus.GRANTED);
        return consents.stream().map(consent -> {
            User patient = userRepo.findById(consent.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
            return new PatientSummary(consent.getId(), patient.getId(), patient.getUsername(), patient.getEmail(),
                    consent.getId());
        }).toList();
    }

    // Method to see all doctors a patient has given consent to
    public List<DoctorSummary> getDoctorSummaries() {
        Long authenticatedPatientId = authenticatedUserProvider.getAuthenticatedUserId();
        List <Consent> consents = consentRepo.findByPatientIdAndStatus(authenticatedPatientId, ConsentStatus.GRANTED);
        return consents.stream().map(consent -> {
            User doctor = userRepo.findById(consent.getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));
            return new DoctorSummary(consent.getId(), doctor.getId(), doctor.getUsername(), doctor.getEmail());
        }).toList();

    }

}
