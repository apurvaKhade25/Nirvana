package com.healthapp.Nirvana.Consent;

import com.healthapp.Nirvana.Consent.Dto.InviteRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
