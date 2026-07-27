package com.healthapp.Nirvana.Consent;

import com.healthapp.Nirvana.Consent.Dto.DoctorSummary;
import com.healthapp.Nirvana.Consent.Dto.InviteRequest;
import com.healthapp.Nirvana.Consent.Dto.PatientSummary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/consent")
public class ConsentController {
    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/invite")
    public String invite(@RequestBody InviteRequest request) {
        consentService.inviteDoctor(request.getDoctorEmail());
        return "Invite sent";
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/{consentId}/revoke")
    public String revoke(@PathVariable Long consentId) {
        consentService.revoke(consentId);
        return "Consent revoked";
    }

    // GET /consent/doctor/patients
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/my-patients")
    public List<PatientSummary> getPatientSummaries() {
        return consentService.getPatientSummaries();
    }

    // GET /consent/patient/{patientId}/doctors
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/my-doctors")
    public List<DoctorSummary> getDoctorSummaries() {
        return consentService.getDoctorSummaries();
    }
}
